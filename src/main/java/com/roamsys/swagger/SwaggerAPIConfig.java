package com.roamsys.swagger;

import com.roamsys.swagger.annotations.SwaggerApi;
import com.roamsys.swagger.annotations.SwaggerModel;
import com.roamsys.swagger.annotations.SwaggerParameter;
import com.roamsys.swagger.data.SwaggerAPIModelData;
import com.roamsys.swagger.data.SwaggerAPIParameterData;
import com.roamsys.swagger.data.SwaggerExceptionHandler;
import com.roamsys.swagger.documentation.ApiSpecBuilder;
import com.roamsys.swagger.documentation.SwaggerApiSpec;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;

/**
 * The swagger configuration.
 *
 * @author johanna
 */
public class SwaggerAPIConfig {

    /**
     * Content type for JavaScript
     */
    public final static String CONTENT_TYPE_JAVASCRIPT = "text/javascript";

    /**
     * The swagger API servlet context attribute name
     */
    public final static String SERVLET_ATTRIBUTE_NAME = "SwaggerAPIModel";

    /**
     * The map that holds all swagger API data with their path as identifier
     */
    private final Map<String, List<SwaggerAPIModelData>> swaggerAPIs = new HashMap<>();

    /**
     * The builder for collecting API spec information
     */
    private final ApiSpecBuilder apiSpecBuilder;

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
     * The context of the servlet session
     */
    private final ServletContext servletContext;

    /**
     * Constructor to initialize values
     * @param servletContext the servlet context
     */
    public SwaggerAPIConfig(final ServletContext servletContext) {
        this.servletContext = servletContext;
        apiSpecBuilder = new ApiSpecBuilder();
    }

    /**
     * Registers a new API model
     *
     * @param model new Swagger API model
     */
    public void registerModel(final SwaggerAPIModel model) {
        // check if current model is annotated with {@link SwaggerModel}
        final SwaggerModel modelAnnotation = model.getClass().getAnnotation(SwaggerModel.class);
        if (modelAnnotation != null) {
            final String modelPath = modelAnnotation.path() + "." + modelAnnotation.format();

            for (final Method method : model.getClass().getMethods()) {
                if (method.isAnnotationPresent(SwaggerApi.class)) {

                    // fetch Swagger annotations for the method and it's parameters and prepare the data structures for them
                    method.setAccessible(true);
                    final SwaggerApi annotation = method.getAnnotation(SwaggerApi.class);
                    final Annotation[][] annotations = method.getParameterAnnotations();
                    final List<SwaggerParameter> paramAnnotations = new ArrayList<>(annotations.length);

                    // collect the parameter annotations
                    for (final Annotation[] param : annotations) {
                        for (final Annotation currentParamAnnotation : param) {
                            if (currentParamAnnotation.annotationType().equals(SwaggerParameter.class)) {
                                final SwaggerParameter paramAnnotaion = (SwaggerParameter) currentParamAnnotation;
                                paramAnnotations.add(paramAnnotaion);
                            }
                        }
                    }

                    // add the data structure with the collected information to the list of APIs for the current base path
                    final String path = modelPath + annotation.path();
                    final List<SwaggerAPIParameterData> parameters = paramAnnotations.stream().map(a -> new SwaggerAPIParameterData(a.name(), a.paramType(), a.dataType())).collect(Collectors.toList());
                    swaggerAPIs.computeIfAbsent(modelPath, p -> new ArrayList<>()).add(new SwaggerAPIModelData(model, method, annotation.method(), path, parameters));

                    // add the API operation to spec
                    apiSpecBuilder.addOperation(modelAnnotation, annotation, paramAnnotations);
                }
            }
        } else {
            throw new IllegalArgumentException(SwaggerAPIModel.class.getSimpleName() + " annotation must be present on model class");
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
     * Get the OpenAPI spec for the configured models.
     *
     * @return the API spec
     */
    public SwaggerApiSpec getApiSpec() {
        return apiSpecBuilder.getApiSpec();
    }

    /**
     * Sets the URL serving the API. This field is important for completing the OpenAPI specification.
     * Declarations on the server providing the APIs themselves, it is not a requirement.
     *
     * @param apiUrl The API URL
     */
    public void setURL(final URL apiUrl) {
        apiSpecBuilder.setSchemes(Collections.singletonList(apiUrl.getProtocol()));
        if (apiUrl.getPort() != -1) {
            apiSpecBuilder.setHost(String.format("%s:%d", apiUrl.getHost(), apiUrl.getPort()));
        } else {
            apiSpecBuilder.setHost(apiUrl.getHost());
        }
        apiSpecBuilder.setBasePath(apiUrl.getPath());
    }

    /**
     * Provides the version of the application API.
     *
     * @param apiVersion the API version as string
     */
    public void setAPIVersion(final String apiVersion) {
        apiSpecBuilder.setVersion(apiVersion);
    }

    /**
     * Provides title metadata about the API. The title can be used by the clients if needed, and can be presented in the
     * Swagger-UI for convenience.
     *
     * @param info the swagger metadata info as string
     */
    public void setTitle(final String title) {
        apiSpecBuilder.setTitle(title);
    }

    /**
     * Provides description metadata about the API. This field is important for completing the OpenAPI specification.
     *
     * @param info the swagger metadata info as string
     */
    public void setDescription(final String description) {
        apiSpecBuilder.setDescription(description);
    }

    /**
     * Returns the context of the Swagger API servlet
     * @return the servlet context
     */
    public ServletContext getServletContext() {
        return servletContext;
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
