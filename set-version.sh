#!/bin/sh
set -eu

USE_DEV_SNAPSHOT=0

while [ "$#" -gt 0 ]; do
  case "$1" in
    --development-snapshot)
      USE_DEV_SNAPSHOT=1
      ;;
    -h|--help)
      echo "Usage: $0 [--development-snapshot]"
      exit 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      exit 1
      ;;
  esac
  shift
done

BRANCH="${CI_COMMIT_BRANCH:-${CI_COMMIT_REF_NAME:-${CI_REPO_BRANCH:-${WOODPECKER_REPO_BRANCH:-${DRONE_BRANCH:-unknown}}}}}"
BUILD_NUMBER="${CI_PIPELINE_NUMBER:-${CI_PIPELINE_ID:-${CI_BUILD_NUMBER:-${CI_BUILD_ID:-${WOODPECKER_BUILD_NUMBER:-${WOODPECKER_BUILD_ID:-${DRONE_BUILD_NUMBER:-${DRONE_BUILD_ID:-${DRONE_BUILD:-0}}}}}}}}}"
if [ -z "$BUILD_NUMBER" ]; then
  BUILD_NUMBER=0
fi

POM_VERSION="$(mvn -B help:evaluate -Dexpression=project.version -q -DforceStdout)"
if [ -z "$POM_VERSION" ]; then
  echo "Unable to determine Maven project version" >&2
  exit 1
fi

BASE_VERSION="$POM_VERSION"
case "$BASE_VERSION" in
  *-SNAPSHOT)
    BASE_VERSION="${BASE_VERSION%-SNAPSHOT}"
    ;;
esac

if [ -z "$BASE_VERSION" ]; then
  BASE_VERSION="$POM_VERSION"
fi

# Determine new version. Note: pom.xml is reused in all steps. If the 'test' step has set the version, the 'deploy' step must not do so again (idempotent).
if printf '%s' "$BRANCH" | grep -Eq '^uat(-|$)'; then
  # Add -RC only if not present already
  if echo "$BASE_VERSION" | grep -qv -- '-RC$'; then
    NEW_VERSION="${BASE_VERSION}-RC"
  else
    NEW_VERSION="$BASE_VERSION"
  fi

elif [ "$BRANCH" = "master" ]; then
  NEW_VERSION="$BASE_VERSION"

elif [ "$USE_DEV_SNAPSHOT" -eq 1 ] && printf '%s' "$BRANCH" | grep -Eq '^development(-|$)'; then
  # Add -SNAPSHOT only if not present
  if echo "$BASE_VERSION" | grep -qv -- '-SNAPSHOT$'; then
    NEW_VERSION="${BASE_VERSION}-SNAPSHOT"
  else
    NEW_VERSION="$BASE_VERSION"
  fi

else
  # Add -b123 only if not present
  if echo "$BASE_VERSION" | grep -q -- '-b[0-9]\+$'; then
    NEW_VERSION="$BASE_VERSION"
  else
    NEW_VERSION="${BASE_VERSION}-b${BUILD_NUMBER}"
  fi
fi

echo "Setting Maven project version to ${NEW_VERSION} (from ${POM_VERSION}) for branch ${BRANCH}"
mvn -B versions:set -DnewVersion="$NEW_VERSION"

# dependency version cleanup
POM_FILE="pom.xml"
if printf '%s' "$BRANCH" | grep -Eq '^uat(-|$)'; then
  echo "Modification of dependency versions: Replacing -SNAPSHOT → -RC"
  sed -i 's/-SNAPSHOT/-RC/g' "$POM_FILE"
elif [ "$BRANCH" = "master" ]; then
  echo "Modification of dependency versions: Removing -SNAPSHOT"
  sed -i 's/-SNAPSHOT//g' "$POM_FILE"
fi
