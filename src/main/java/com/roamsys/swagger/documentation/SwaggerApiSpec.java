package com.roamsys.swagger.documentation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

/**
 * OpenAPI 2.0 Swagger specification.
 *
 * @author nbecker
 */
public class SwaggerApiSpec {

    @Expose
    @SerializedName("swagger")
    String swaggerVersion;

    @Expose
    InfoApiSpec info;

    @Expose
    String host, basePath;

    @Expose
    List<String> schemes;

    @Expose
    Map<String, Map<String, String>> securityDefinitions;

    @Expose
    List<TagApiSpec> tags;

    @Expose
    Map<String, Map<String, OperationApiSpec>> paths;

    @Override
    public String toString() {
        return String.format("Info: %s, Paths: %s", info, paths.keySet());
    }
}
