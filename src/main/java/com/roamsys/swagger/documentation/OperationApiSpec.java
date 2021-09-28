package com.roamsys.swagger.documentation;

import com.google.gson.annotations.Expose;
import java.util.List;
import java.util.Map;

/**
 * OpenAPI operation block specification.
 *
 * @author nbecker
 */
public class OperationApiSpec extends AbstractApiSpecPart {

    @Expose
    String summary, operationId;

    @Expose
    List<String> tags;

    @Expose
    List<String> produces;

    @Expose
    List<ParameterApiSpec> parameters;

    @Expose
    Map<Integer, ResponseApiSpec> responses;

    @Expose
    List<Map<String, List<String>>> security;

}
