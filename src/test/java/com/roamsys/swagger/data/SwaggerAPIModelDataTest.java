package com.roamsys.swagger.data;

import com.roamsys.swagger.SwaggerAPIConfig;
import com.roamsys.swagger.TestsAPI;
import com.roamsys.swagger.annotations.SwaggerApi;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for {@link SwaggerAPIModelData} and {@link SwaggerAPIConfig}.
 *
 * @author nbecker
 */
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
}
