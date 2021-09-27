package com.roamsys.swagger.documentation;

import com.google.gson.annotations.Expose;
import java.util.Map;

/**
 * OpenAPI parameter block specification.
 *
 * @author nbecker
 */
public class ParameterApiSpec extends AbstractApiSpecPart {

    @Expose
    String name, in;

    @Expose
    boolean required;

    @Expose
    String type;

    @Expose
    Map<String, String> schema;
}
