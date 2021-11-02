package com.roamsys.swagger.documentation;

import com.google.gson.annotations.Expose;

/**
 * OpenAPI info block specification.
 *
 * @author nbecker
 */
public class InfoApiSpec extends AbstractApiSpecPart {

    @Expose
    String version, title;

    @Override
    public String toString() {
        return String.format("%s (Version %s)", title, version);
    }
}