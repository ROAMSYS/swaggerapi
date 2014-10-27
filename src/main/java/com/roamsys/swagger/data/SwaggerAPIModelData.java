package com.roamsys.swagger.data;

import com.roamsys.swagger.SwaggerAPIModel;
import com.roamsys.swagger.annotations.SwaggerApi.HTTPMethod;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
    private final Pattern path;

    /**
     * HTTP method type
     */
    private final HTTPMethod httpMethod;

    /**
     * Parameter data
     */
    private final ArrayList<SwaggerAPIParameterData> paramData;

    /**
     * The standard parameter pattern
     */
    final private static String PATTERN = "\\{[0-9a-zA-Z]+\\}";

    /**
     * Creates a new wrapper for swagger API
     *
     * @param modelClass the class of the API model
     * @param method the method
     * @param httpMethod the HTTP method
     * @param path the entire path of the API method
     * @param paramData the detailed data for the method parameters
     */
    public SwaggerAPIModelData(final SwaggerAPIModel modelClass, final Method method, final HTTPMethod httpMethod, final String path, final ArrayList<SwaggerAPIParameterData> paramData) {
        this.modelClass = modelClass;
        this.method = method;
        this.httpMethod = httpMethod;
        this.path = Pattern.compile(path.replaceAll(PATTERN, "(.+)"));
        this.paramData = paramData;
    }

    /**
     * Returns the matcher for the given path with the current swagger API path
     *
     * @param path full swagger API path as string
     * @return matcher
     */
    public Matcher matchPath(final String path) {
        return this.path.matcher(path);
    }

    /**
     * Returns if given method equals HTTP method of current swagger API
     *
     * @param method HTTP method
     * @return true if given method equals HTTP method of swagger API
     */
    public boolean hasHTTPMethod(final HTTPMethod method) {
        return this.httpMethod == method;
    }

    /**
     * Returns the reflection method of current swagger API
     *
     * @return method
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
    public ArrayList<SwaggerAPIParameterData> getParameterDetails() {
        return paramData;
    }
}
