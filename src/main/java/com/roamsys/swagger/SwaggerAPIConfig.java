package com.roamsys.swagger;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.roamsys.swagger.annotations.SwaggerApi;
import com.roamsys.swagger.annotations.SwaggerModel;
import com.roamsys.swagger.annotations.SwaggerParameter;
import com.roamsys.swagger.data.SwaggerAPIModelData;
import com.roamsys.swagger.data.SwaggerAPIParameterData;
import com.roamsys.swagger.data.SwaggerExceptionHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The swagger configuration
 *
 * @author johanna
 */
public class SwaggerAPIConfig {

    /**
     * Content type for JavaScript
     */
    final public static String CONTENT_TYPE_JAVASCRIPT = "text/javascript";

    /**
     * The swagger API servlet context attribute name
     */
    final public static String SERVLET_ATTRIBUTE_NAME = "SwaggerAPIModel";

    /**
     * The map that holds all swagger API data with their path as identifier
     */
    final private Map<String, List<SwaggerAPIModelData>> swaggerAPIs = new HashMap<String, List<SwaggerAPIModelData>>();

    /**
     * The map that holds all swagger API definitions with their path as identifier
     */
    final private Map<String, List<String>> resourcesAPIDefs = new HashMap<String, List<String>>();

    /**
     * The basic JSON for API documentation (data for resource.json)
     */
    final private JSONObject resources;

    /**
     * List of API doc models included in resources.json
     */
    final private Map<String, JSONObject> resourcesAPIs;

    /**
     * Defines if cross origin access is allowed
     */
    private boolean allowOriginAccess = false;

    /**
     * The default content type for the HTTP response
     */
    private String defaultContentType;

    /**
     * The exception handler
     */
    private SwaggerExceptionHandler exceptionHandler = SwaggerExceptionHandler.DEFAULT;

    /**
     * The handler used to authenticate the API calls
     */
    private SwaggerAPIAuthorizationHandler authorizationHandler;

    /**
     * The handler that is executed before each request after the authentication
     */
    private SwaggerAPICustomRequestHandler preRequestHandler;

    /**
     * The handler that is executed after each request
     */
    private SwaggerAPICustomRequestHandler postRequestHandler;

    /**
     * Constructor to initialize values
     */
    public SwaggerAPIConfig() {
        resources = new JSONObject();
        resourcesAPIs = new HashMap<String, JSONObject>();
    }

    /**
     * Registers a new API model
     *
     * @param model new Swagger API model
     */
    public void registerModel(final SwaggerAPIModel model) {
        // Check if current model is annotated with {@link SwaggerModel}
        if (model.getClass().isAnnotationPresent(SwaggerModel.class)) {
            try {
                final SwaggerModel modelAnnotation = model.getClass().getAnnotation(SwaggerModel.class);
                final String modelPath = modelAnnotation.path() + "." + modelAnnotation.format();

                // add model only once
                if (!resourcesAPIs.containsKey(modelPath) && modelAnnotation.format().equals("json")) {
                    final JSONObject resourceModel = new JSONObject();
                    resourceModel.put("path", modelAnnotation.path() + ".{format}");
                    if (!modelAnnotation.description().isEmpty()) {
                        resourceModel.put("description", modelAnnotation.description());
                    }
                    resourcesAPIs.put(modelPath, resourceModel);
                }

                for (final Method method : model.getClass().getMethods()) {
                    if (method.isAnnotationPresent(SwaggerApi.class)) {

                        // fetch Swagger API annotations and make sure that the hash maps are prepared for the base path of API
                        final SwaggerApi annotation = method.getAnnotation(SwaggerApi.class);
                        if (!swaggerAPIs.containsKey(modelPath)) {
                            swaggerAPIs.put(modelPath, new LinkedList<SwaggerAPIModelData>());
                            resourcesAPIDefs.put(modelPath, new LinkedList<String>());
                        }

                        // create resource.json content for the API call corresponding to the class method
                        final JSONObject operation = new JSONObject();
                        operation.put("httpMethod", annotation.method());
                        operation.put("summary", annotation.summary());
                        operation.put("nickname", method.getDeclaringClass().getSimpleName() + "_" + method.getName());
                        operation.put("notes", annotation.notes());

                        // fetch annotations for the method's parameters and prepare the data structures for them
                        final Annotation[][] annotations = method.getParameterAnnotations();
                        final JSONArray parameters = new JSONArray();
                        final ArrayList<SwaggerAPIParameterData> paramData = new ArrayList<SwaggerAPIParameterData>(annotations.length);

                        // create the resource.json content for the method parameters and add the parameter types to the data structure
                        for (final Annotation[] param : annotations) {
                            for (final Annotation currentParamAnnotation : param) {
                                if (currentParamAnnotation.annotationType().equals(SwaggerParameter.class)) {
                                    final SwaggerParameter paramAnnotaion = (SwaggerParameter) currentParamAnnotation;
                                    paramData.add(new SwaggerAPIParameterData(paramAnnotaion.name(), paramAnnotaion.paramType(), paramAnnotaion.dataType()));

                                    final JSONObject currentParam = new JSONObject();
                                    currentParam.put("name", paramAnnotaion.name());
                                    currentParam.put("required", paramAnnotaion.required());
                                    currentParam.put("allowMultiple", paramAnnotaion.allowMultiple());
                                    currentParam.put("dataType", paramAnnotaion.dataType());
                                    currentParam.put("description", paramAnnotaion.description());
                                    currentParam.put("paramType", paramAnnotaion.paramType());
                                    parameters.put(currentParam);
                                }
                            }
                        }
                        operation.put("parameters", parameters);

                        // add the data structure with the collected information to the list of APIs for the current base path
                        final String path = modelPath + annotation.path();
                        swaggerAPIs.get(modelPath).add(new SwaggerAPIModelData(model, method, annotation.method(), path, paramData));

                        // create the resource.json content for the collection information
                        final JSONObject api = new JSONObject();
                        api.put("operations", new JSONArray().put(operation));
                        api.put("path", path);
                        if (!annotation.description().isEmpty()) {
                            api.put("description", annotation.description());
                        }
                        resourcesAPIDefs.get(modelPath).add(api.toString());
                    }
                }
            } catch (final JSONException ex) {
                throw new IllegalArgumentException("Error accessing JSON", ex);
            }
        }
    }

    /**
     * Get swagger API list for current base path
     *
     * @param path the base path to the API model
     * @return list of swagger API data that belong to the base path
     */
    public List<SwaggerAPIModelData> getAPIsFor(final String path) {
        return swaggerAPIs.get(path);
    }

    /**
     * Returns the API model for the current API path
     *
     * @param path the path that defines the API model
     * @return JSON
     */
    public String getAPIModelFor(final String path) {
        return "{\"apis\": [" + StringUtils.join(resourcesAPIDefs.get(path), ", ") + "]}";
    }

    /**
     * Adds a value to the resources JSON
     * @param key the property key
     * @param value the value
     */
    private void addToResourcesJSON(final String key, final String value) {
        try {
            resources.put(key, value);
        } catch (final JSONException ex) {
            throw new IllegalArgumentException("Error accessing JSON", ex);
        }
    }

    /**
     * The root URL serving the API. This field is important as while it is common to have the Resource Listing and API
     * Declarations on the server providing the APIs themselves, it is not a requirement.
     *
     * @param basePath The value should be in the format of a URL.
     */
    public void setBasePath(final String basePath) {
        addToResourcesJSON("basePath", basePath.endsWith("/") ? basePath : basePath + '/');
    }

    /**
     * Provides the version of the application API.
     *
     * @param apiVersion the API version as string
     */
    public void setAPIVersion(final String apiVersion) {
        addToResourcesJSON("apiVersion", apiVersion);
    }

    /**
     * Specifies the Swagger Specification version being used. It can be used by the Swagger UI and other clients to
     * interpret the API listing.
     *
     * @param swaggerVersion The value MUST be an existing Swagger specification version
     */
    public void setSwaggerVersion(final String swaggerVersion) {
        addToResourcesJSON("swaggerVersion", swaggerVersion);
    }

    /**
     * Provides metadata about the API. The metadata can be used by the clients if needed, and can be presented in the
     * Swagger-UI for convenience.
     *
     * @param info the swagger metadata info as string
     */
    public void setInfo(final String info) {
        addToResourcesJSON("info", info);
    }

    /**
     * Returns the basic JSON containing the API data for resource.json
     *
     * @return resources JSON object
     */
    public JSONObject getAPIDoc() {
        try {
            resources.put("apis", resourcesAPIs.values());
        } catch (final JSONException ex) {
            throw new IllegalArgumentException("Error accessing JSON", ex);
        }
        return resources;
    }

    /**
     * Returns if current path belongs to an API model definition
     *
     * @param path the current API path
     * @return true if current path defines an API model
     */
    public boolean isAPIModelPath(final String path) {
        return swaggerAPIs.containsKey(path);
    }

    /**
     * Allows cross origin access to the API. Is not allowed by default.
     */
    public void allowCrossOriginAccess() {
        allowOriginAccess = true;
    }

    /**
     * Returns if cross origin access to the API is allowed.
     *
     * @return true if cross origin access is enabled
     */
    public boolean isCrossOriginAccessAllowed() {
        return allowOriginAccess;
    }

    /**
     * Sets the default content type for the HTTP response
     *
     * @param defaultContentType
     */
    public void setDefaultContentType(final String defaultContentType) {
        this.defaultContentType = defaultContentType;
    }

    /**
     * Returns default content type for the HTTP response
     *
     * @return default content type for the HTTP response
     */
    public String getDefaultContentType() {
        return defaultContentType;
    }

    /**
     * Returns the exception handler for handling the exceptions thrown while method invocations
     * @return the exception handler or null if none is set
     */
    public SwaggerExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Sets an exception handler for handling exceptions thrown while method invocations
     * @param exceptionHandler the exception handler
     */
    public void setExceptionHandler(final SwaggerExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Sets the authorization handler to authenticate the API calls
     * @param authorizationHandler the authorization handler (set to null to disable authorization)
     */
    public void setAuthorizationHandler(final SwaggerAPIAuthorizationHandler authorizationHandler) {
        this.authorizationHandler = authorizationHandler;
    }

    /**
     * Returns the authentication handler, which is called before every time before a request is handled
     * @return the authentication handler
     */
    public SwaggerAPIAuthorizationHandler getAuthorizationHandler() {
        return authorizationHandler;
    }

    /**
     * Returns the pre-request handler, that is executed before each API request (after the authentication)
     * @return the pre request handler
     */
    public SwaggerAPICustomRequestHandler getPreRequestHandler() {
        return preRequestHandler;
    }

    /**
     * Sets the pre-request handler, that is executed before each API request (after the authentication)
     * @param preRequestHandler the pre request hander (set to null to disable)
     */
    public void setPreRequestHandler(final SwaggerAPICustomRequestHandler preRequestHandler) {
        this.preRequestHandler = preRequestHandler;
    }

    /**
     * Returns the post-request handler, that is executed after each API request
     * @return the post request handler
     */
    public SwaggerAPICustomRequestHandler getPostRequestHandler() {
        return postRequestHandler;
    }

    /**
     * Sets the post-request handler, that is executed after each API request
     * @param postRequestHandler the post request hander (set to null to disable)
     */
    public void setPostRequestHandler(final SwaggerAPICustomRequestHandler postRequestHandler) {
        this.postRequestHandler = postRequestHandler;
    }

}
