package com.roamsys.swagger.documentation;

import com.google.gson.annotations.Expose;

/**
 * Base class of an OpenAPI specification block.
 * Every block should have at least a description property.
 *
 * @author nbecker
 */
public class AbstractApiSpecPart {

    @Expose
    String description;
}
