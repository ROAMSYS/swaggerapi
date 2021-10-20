package com.roamsys.swagger.data;

import com.roamsys.swagger.SwaggerAPIModel;
import com.roamsys.swagger.annotations.SwaggerApi.HTTPMethod;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holds the data for a swagger API
 *
 * @author johanna
 */
public class SwaggerAPIModelData {

    /**
     * Instance of the swagger API model
     */
    private final SwaggerAPIModel modelClass;
    /**
     * The reflection method
     */
    private final Method method;
     /**
     * The path pattern
     */
    private final Pattern pathPattern;

    /**
     * HTTP method type
     */
    private final HTTPMethod httpMethod;

    /**
     * Parameter data
     */
    private final List<SwaggerAPIParameterData> parameters;

    /**
     * The standard parameter pattern
     */
    private static final String PATTERN = "\\{[0-9a-zA-Z]+\\}";

    /**
     * Creates a new wrapper for swagger API
     *
     * @param modelClass the class of the API model
     * @param method the method
     * @param httpMethod the HTTP method
     * @param path the entire path of the API method
     * @param parameters the detailed data for the method parameters
     */
    public SwaggerAPIModelData(final SwaggerAPIModel modelClass, final Method method, final HTTPMethod httpMethod, final String path, final List<SwaggerAPIParameterData> parameters) {
        this.modelClass = modelClass;
        this.method = method;
        this.httpMethod = httpMethod;
        this.pathPattern = Pattern.compile(path.replaceAll(PATTERN, "(\\[^/\\]+)"));
        this.parameters = parameters;
    }

    /**
     * Returns the matcher for the given path with the current swagger API path
     *
     * @param path full swagger API path as string
     * @return matcher
     */
    public Matcher matchPath(final String path) {
        return this.pathPattern.matcher(path);
    }

    /**
     * Returns the HTTP method of current swagger API
     *
     * @return HTTP method
     */
    public HTTPMethod getHTTPMethod() {
        return httpMethod;
    }

    /**
     * Returns the reflection method of current swagger API
     *
     * @return reflection method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Returns the class of the parent swagger API model
     *
     * @return instance of swagger API model
     */
    public SwaggerAPIModel getAPIModelClass() {
        return modelClass;
    }

    /**
     * Returns detailed data about parameters
     *
     * @return parameter details
     */
    public List<SwaggerAPIParameterData> getParameters() {
        return parameters;
    }

}
