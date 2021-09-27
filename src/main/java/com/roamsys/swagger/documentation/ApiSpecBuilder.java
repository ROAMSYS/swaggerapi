package com.roamsys.swagger.documentation;

import com.roamsys.swagger.annotations.SwaggerApi;
import com.roamsys.swagger.annotations.SwaggerModel;
import com.roamsys.swagger.annotations.SwaggerParameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder for creating OpenAPI Swagger documentation/specification based on metadata/code annotations.
 *
 * @author nbecker
 */
public class ApiSpecBuilder {

    /**
     * The currently used OpenAPI version: <code>swagger: "2.0"</code>
     */
    private static final String SWAGGER_VERSION = "2.0";

    // defaults for several structures that are declared static in context of this framework
    private static final String DEFAULT_TITLE = "Swagger API";
    private static final List<String> DEFAULT_SCHEMES = Collections.singletonList("https");
    private static final Map<String, Map<String, String>> DEFAULT_SECURITY_DEFINITION = Collections.singletonMap("api_key", Map.of("type", "apiKey", "name", "x-api-key", "in", "header"));
    private static final List<Map<String, List<String>>> DEFAULT_SECURITY = Collections.singletonList(Collections.singletonMap("api_key", Collections.emptyList()));
    private static final Map<String, String> DEFAULT_OBJECT_SCHEMA = Collections.singletonMap("type", "object");

    private final SwaggerApiSpec apiSpec;

    /**
     * Constructor.
     */
    public ApiSpecBuilder() {
        apiSpec = new SwaggerApiSpec();
        apiSpec.swaggerVersion = SWAGGER_VERSION;
        apiSpec.info = new InfoApiSpec();
        apiSpec.info.title = DEFAULT_TITLE;
        apiSpec.basePath = "/";
        apiSpec.schemes = DEFAULT_SCHEMES;
        apiSpec.securityDefinitions = DEFAULT_SECURITY_DEFINITION;
        apiSpec.paths = new HashMap<>();
    }

    /**
     * @return the created specification
     */
    public SwaggerApiSpec getApiSpec() {
        return apiSpec;
    }

    /**
     * Sets the host to be shown and also used for generating test URLs.
     *
     * @param host the host
     * @return <code>this</code> pointer
     */
    public ApiSpecBuilder setHost(final String host) {
        apiSpec.host = host;
        return this;
    }

    /**
     * Sets the base path to be shown and also used for generating test URLs.
     *
     * @param host the host
     * @return <code>this</code> pointer
     */
    public ApiSpecBuilder setBasePath(final String basePath) {
        apiSpec.basePath = basePath;
        return this;
    }

    /**
     * Sets the version to be shown in info block.
     *
     * @param version the version
     * @return <code>this</code> pointer
     */
    public ApiSpecBuilder setVersion(final String version) {
        apiSpec.info.version = version;
        return this;
    }

    /**
     * Sets the title to be shown in info block.
     *
     * @param version the title
     * @return <code>this</code> pointer
     */
    public ApiSpecBuilder setTitle(final String title) {
        apiSpec.info.title = title;
        return this;
    }

    /**
     * Sets the description to be shown in info block.
     *
     * @param description the version
     * @return <code>this</code> pointer
     */
    public ApiSpecBuilder setDescription(final String description) {
        apiSpec.info.description = description;
        return this;
    }

    /**
     * Adds an operation by generating and using the related path. Multiple operations may be added for same path but with different HTTP method.
     *
     * @param modelAnnotation the model annotation
     * @param apiAnnotation the API call annotation
     * @param parameterAnnotations the list of parameter annotations of the API call
     * @return <code>this</code> pointer
     */
    public ApiSpecBuilder addOperation(final SwaggerModel modelAnnotation, final SwaggerApi apiAnnotation, final List<SwaggerParameter> parameterAnnotations) {
        final String modelName = cleanupPath(modelAnnotation.path());
        final String path = String.format("%s.%s%s", modelAnnotation.path(), modelAnnotation.format(), apiAnnotation.path());
        // add operation using given path and method
        apiSpec.paths.computeIfAbsent(path, p -> new HashMap<>()).put(apiAnnotation.method().toString(), createOperatorSpec(modelName, modelAnnotation, apiAnnotation, parameterAnnotations));
        // add a tag for providing description
        if (!modelAnnotation.description().isEmpty()) {
            if (apiSpec.tags == null) {
                apiSpec.tags = new ArrayList<>();
            }
            // tag maybe created by former call for another call for same model
            if (apiSpec.tags.stream().noneMatch(t -> modelName.equalsIgnoreCase(t.name))) {
                final TagApiSpec tagSpec = new TagApiSpec();
                tagSpec.name = modelName;
                tagSpec.description = modelAnnotation.description();
                apiSpec.tags.add(tagSpec);
            }
        }
        return this;
    }

    /**
     * Helper for creating operator specification block.
     */
    private OperationApiSpec createOperatorSpec(final String modelName, final SwaggerModel modelAnnotation, final SwaggerApi apiAnnotation, final List<SwaggerParameter> parameterAnnotations) {
        final OperationApiSpec operationSpec = new OperationApiSpec();
        operationSpec.description = apiAnnotation.description().isEmpty() ? apiAnnotation.notes() : String.format("\n%s\n*Notes: %s*", apiAnnotation.description(), apiAnnotation.notes());
        operationSpec.operationId = modelName + cleanupPath(apiAnnotation.path());
        operationSpec.tags = Collections.singletonList(modelName);
        operationSpec.produces = Collections.singletonList("application/" + modelAnnotation.format());
        operationSpec.summary = apiAnnotation.summary();
        // add all parameters
        operationSpec.parameters = new ArrayList<>();
        for (final SwaggerParameter parameterAnnotation : parameterAnnotations) {
            operationSpec.parameters.add(createParamterSpec(parameterAnnotation));
        }
        operationSpec.responses = new HashMap<>();
        operationSpec.responses.put(403, createResponseSpec("authentication failed"));
        operationSpec.security = DEFAULT_SECURITY;
        return operationSpec;
    }

    /**
     * Helper for creating parameter specification block.
     */
    private ParameterApiSpec createParamterSpec(final SwaggerParameter parameterAnnotation) {
        final ParameterApiSpec parameterSpec = new ParameterApiSpec();
        parameterSpec.name = parameterAnnotation.name();
        parameterSpec.required = parameterAnnotation.required();
        parameterSpec.description = parameterAnnotation.description();
        parameterSpec.in = parameterAnnotation.paramType().toString();
        switch (parameterAnnotation.dataType()) {
            case DATA:
                parameterSpec.schema = DEFAULT_OBJECT_SCHEMA;
                break;
            case DATE:
            case DATETIME:
                // Date is not a known type in OpenAPI/JSON
                parameterSpec.type = SwaggerParameter.DataType.STRING.toString();
                break;
            default:
                parameterSpec.type = parameterAnnotation.dataType().toString();
                break;
        }
        return parameterSpec;
    }

    /**
     * Helper for creating response specification block.
     */
    private ResponseApiSpec createResponseSpec(final String description) {
        final ResponseApiSpec responseSpec = new ResponseApiSpec();
        responseSpec.description = description;
        return responseSpec;
    }

    /**
     * Helper for removing slashes and parentheses from path.
     */
    private String cleanupPath(final String path) {
        return path.replaceAll("\\/|\\{|\\}", "");
    }

}
