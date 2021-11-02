package com.roamsys.swagger.documentation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.roamsys.swagger.TestsAPI;
import com.roamsys.swagger.annotations.SwaggerApi;
import com.roamsys.swagger.annotations.SwaggerModel;
import com.roamsys.swagger.data.SwaggerAPIContext;
import java.lang.reflect.Method;
import java.util.Collections;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link ApiSpecBuilder}.
 *
 * @author nbecker
 */
public class ApiSpecBuilderTest {

    @Test
    public void testApiSpec() throws Exception {
        final ApiSpecBuilder apiSpecBuilder = new ApiSpecBuilder();
        final Method testMethod = TestsAPI.class.getMethod("test", SwaggerAPIContext.class);
        apiSpecBuilder.addOperation(TestsAPI.class.getAnnotation(SwaggerModel.class), testMethod.getAnnotation(SwaggerApi.class), Collections.emptyList());
        final Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        final JsonElement specJson = gson.toJsonTree(apiSpecBuilder.getApiSpec());
        Assert.assertEquals(specJson, gson.fromJson(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("swagger.json"), "UTF-8"), JsonElement.class));
    }
}
