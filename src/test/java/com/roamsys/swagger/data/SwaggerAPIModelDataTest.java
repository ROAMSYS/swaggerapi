package com.roamsys.swagger.data;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.roamsys.swagger.SwaggerAPIConfig;
import com.roamsys.swagger.SwaggerAPIModel;
import com.roamsys.swagger.annotations.SwaggerApi;
import com.roamsys.swagger.annotations.SwaggerApi.HTTPMethod;
import com.roamsys.swagger.annotations.SwaggerModel;
import com.roamsys.swagger.annotations.SwaggerParameter;
import com.roamsys.swagger.annotations.SwaggerParameter.DataType;
import com.roamsys.swagger.annotations.SwaggerParameter.ParamType;
import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class SwaggerAPIModelDataTest {

    private final String path;
    private final String expectedPattern;

    @Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "/testAPI.json/test", TestsAPI.PATH_TEST },
                { "/testAPI.json/test/123456789012345678901234", TestsAPI.PATH_TEST_ORGANISATION },
                { "/testAPI.json/test/123456789012345678901234/ROAM0", TestsAPI.PATH_TEST_ORGANISATION_TADIG },
                { "/testAPI.json/test/123456789012345678901234/before/2007-08-31T16:47+00:00", TestsAPI.PATH_TEST_ORGANISATION_BEFORE_DATE },
                { "/testAPI.json/test/123456789012345678901234/after/2007-08-31T16:47+00:00", TestsAPI.PATH_TEST_ORGANISATION_AFTER_DATE } });
    }

    public SwaggerAPIModelDataTest(final String path, final String expectedPattern) {
        this.path = path;
        this.expectedPattern = expectedPattern;
    }

    @Test
    public void testMatchPath() {
        final SwaggerAPIConfig config = new SwaggerAPIConfig(null);
        config.registerModel(new TestsAPI());
        final String basePath = path.substring(0, path.indexOf("/", 1));
        for (final SwaggerAPIModelData api : config.getAPIsFor(basePath)) {
            final boolean isMatch = api.matchPath(path).matches();
            final String currentPattern = api.getMethod().getAnnotation(SwaggerApi.class).path();
            Assert.assertEquals(expectedPattern.equals(currentPattern), isMatch);
        }

    }

    @SwaggerModel(path = "/testAPI")
    private static class TestsAPI implements SwaggerAPIModel {
        public static final String PATH_TEST = "/test";
        public static final String PATH_TEST_ORGANISATION = "/test/{organisationidList}";
        public static final String PATH_TEST_ORGANISATION_BEFORE_DATE = "/test/{organisationidList}/before/{date}";
        public static final String PATH_TEST_ORGANISATION_AFTER_DATE = "/test/{organisationidList}/after/{date}";
        public static final String PATH_TEST_ORGANISATION_TADIG = "/test/{organisationidList}/{tadigList}";

        @SwaggerApi(method = SwaggerApi.HTTPMethod.GET, path = PATH_TEST_ORGANISATION, summary = "", notes = "")
        public void test_Org(final SwaggerAPIContext context,
                @SwaggerParameter(name = "organisationidList", description = "", required = true, paramType = ParamType.PATH, dataType = DataType.STRING) final String organisationidList) {
            //Do nothing
        }

        @SwaggerApi(method = SwaggerApi.HTTPMethod.GET, path = PATH_TEST, summary = "", notes = "")
        public void test(final SwaggerAPIContext context) {
            //Do nothing
        }

        @SwaggerApi(notes = "", method = HTTPMethod.GET, path = PATH_TEST_ORGANISATION_TADIG, summary = "")
        public void test_Org_Tadig(final SwaggerAPIContext context,
                @SwaggerParameter(name = "organisationidList", description = "", required = true, paramType = ParamType.PATH, dataType = DataType.STRING) final String organisationidList,
                @SwaggerParameter(name = "tadigList", description = "", required = true, paramType = ParamType.PATH, dataType = DataType.STRING) final String tadigList) {
            //Do nothing
        }

        @SwaggerApi(notes = "", method = HTTPMethod.GET, path = PATH_TEST_ORGANISATION_AFTER_DATE, summary = "")
        public void test_Org_After_Date(final SwaggerAPIContext context,
                @SwaggerParameter(name = "organisationidList", description = "", required = true, paramType = ParamType.PATH, dataType = DataType.STRING) final String organisationidList,
                @SwaggerParameter(name = "date", description = "", required = true, paramType = ParamType.PATH, dataType = DataType.STRING) final String date) {
            //Do nothing
        }

        @SwaggerApi(notes = "", method = HTTPMethod.GET, path = PATH_TEST_ORGANISATION_BEFORE_DATE, summary = "")
        public void test_Org_Before_Date(final SwaggerAPIContext context,
                @SwaggerParameter(name = "organisationidList", description = "", required = true, paramType = ParamType.PATH, dataType = DataType.STRING) final String organisationidList,
                @SwaggerParameter(name = "date", description = "", required = true, paramType = ParamType.PATH, dataType = DataType.STRING) final String date) {
            //Do nothing
        }
    }
}
