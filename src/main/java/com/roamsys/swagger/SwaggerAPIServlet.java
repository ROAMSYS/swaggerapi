package com.roamsys.swagger;

import com.roamsys.swagger.annotations.SwaggerApi.HTTPMethod;
import com.roamsys.swagger.annotations.SwaggerParameter.DataType;
import com.roamsys.swagger.data.ContentType;
import com.roamsys.swagger.data.SwaggerAPIContext;
import com.roamsys.swagger.data.SwaggerAPIModelData;
import com.roamsys.swagger.data.SwaggerAPIParameterData;
import com.roamsys.swagger.data.SwaggerExceptionHandler;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

/**
 * The Swagger API servlet
 *
 * @author johanna
 */
public class SwaggerAPIServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Date format for API calls
     */
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Date-Time format for API calls
     */
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @param method the HTTP method call type
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(final HttpServletRequest request, final HttpServletResponse response, final HTTPMethod method) throws ServletException, IOException {
        final SwaggerAPIConfig config = (SwaggerAPIConfig) request.getSession().getServletContext().getAttribute(SwaggerAPIConfig.SERVLET_ATTRIBUTE_NAME);

        final String uri = request.getRequestURI();
        final String path = uri.substring(request.getContextPath().length() + request.getServletPath().length(), uri.length());

        // register exception handler for API
        final String exceptionHandlerClass = getServletConfig().getInitParameter("exceptionHandler");
        if (!StringUtils.isEmpty(exceptionHandlerClass)) {
            try {
                final Class<?> clazz = Class.forName(exceptionHandlerClass);
                config.setExceptionHandler((SwaggerExceptionHandler) clazz.newInstance());
            } catch (final Exception ex) {
                //log error with default exception handler
                config.getExceptionHandler().handleException(this, response, HttpServletResponse.SC_BAD_REQUEST, "Could not instantiate Swagger exception handler [" + exceptionHandlerClass + "]. Default exception handler will be used.", ex);
            }
        }

        // enables cross-origin-access, if allowed in config
        if (config.isCrossOriginAccessAllowed()) {
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, UPDATE, OPTIONS");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With");
        }

        // sets the default content type, if defined in config
        if (config.getDefaultContentType() != null) {
            response.setContentType(config.getDefaultContentType());
        }

        /**
         * try to authenticate the API call
         */
        if (config.getAuthorizationHandler() != null && !config.getAuthorizationHandler().isRequestAuthorized(request, response)) {
            config.getExceptionHandler().handleException(this, response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid authorization key", null);
            return;
        }

        // execute pre-request handler
        if (config.getPreRequestHandler() != null) {
            config.getPreRequestHandler().handle(request, response);
        }

        // Basic resource.json
        if (path == null || path.equals("") || path.equals("/") || path.equals("/resources.json")) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(ContentType.JSON_UTF8);
            response.getWriter().println(config.getAPIDoc().toString());

            // API models
        } else if (config.isAPIModelPath(path)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(ContentType.JSON_UTF8);
            response.getWriter().println(config.getAPIModelFor(path));

            // API method calls
        } else {

            // Define base path
            final int bashPathEndPos = path.indexOf("/", 1);
            if (bashPathEndPos == -1) {
                config.getExceptionHandler().handleException(this, response, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Invalid base URL", null);
                return;
            }
            final String basePath = path.substring(0, path.indexOf("/", 1));
            if (config.isAPIModelPath(basePath)) {
                for (final SwaggerAPIModelData api : config.getAPIsFor(basePath)) {
                    // Find matching API model and method
                    if (api.hasHTTPMethod(method)) {
                        final Matcher m = api.matchPath(path);
                        if (m.matches()) {
                            response.setStatus(HttpServletResponse.SC_OK);

                            // Set up variables for parameter collection
                            final ArrayList<SwaggerAPIParameterData> paramsData = api.getParameterDetails();
                            final int parameterCount = paramsData.size();
                            final Object[] arguments = new Object[parameterCount + 1];
                            arguments[0] = new SwaggerAPIContext(this, request, response, config.getExceptionHandler());

                            // Collect parameters
                            int getParamIndex = 0;
                            for (int i = 1; i <= parameterCount; i++) {
                                final SwaggerAPIParameterData paramData = paramsData.get(i - 1);

                                // Fetch and convert argument value
                                try {
                                    switch (paramData.getParamType()) {
                                        case PATH:
                                            if (m.groupCount() >= getParamIndex + 1) {
                                                arguments[i] = convertParamToArgument(paramData.getDataType(), m.group(1 + getParamIndex++));
                                            }
                                            break;

                                        case QUERY:
                                            arguments[i] = convertParamToArgument(paramData.getDataType(), request.getParameter(paramData.getName()));
                                            break;

                                        case FORM:
                                        case BODY:
                                            arguments[i] = request.getInputStream();
                                            break;

                                        default:
                                            arguments[i] = convertParamToArgument(paramData.getDataType(), IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8));
                                    }
                                } catch (final ParseException ex) {
                                    config.getExceptionHandler().handleException(this, response, HttpServletResponse.SC_BAD_REQUEST, "Invalid value for parameter " + paramData.getName(), ex);
                                    return;
                                }
                            }

                            // Try to invoke method
                            try {
                                api.getMethod().invoke(api.getAPIModelClass(), arguments);
                            } catch (final IllegalAccessException ex) {
                                config.getExceptionHandler().handleException(this, response, HttpServletResponse.SC_BAD_REQUEST, "Called method is not visible", ex);
                            } catch (final IllegalArgumentException ex) {
                                config.getExceptionHandler().handleException(this, response, HttpServletResponse.SC_NOT_ACCEPTABLE, "Illegal parameters for called method. See server error log for details.", ex);
                            } catch (final InvocationTargetException ex) {
                                config.getExceptionHandler().handleException(this, response, HttpServletResponse.SC_BAD_REQUEST, "Error calling method. See server error log for details.", ex);
                            } catch (final Throwable ex) {
                                config.getExceptionHandler().handleException(this, response, HttpServletResponse.SC_BAD_REQUEST, "Internal server error for called method. See server error log for details.", ex);
                            }
                            break;
                        }
                    }
                }
            }
        }

        // execute post-request handler
        if (config.getPostRequestHandler() != null) {
            config.getPostRequestHandler().handle(request, response);
        }

        response.flushBuffer();
    }

    /**
     * Convert the swagger API parameter to a method argument class type
     * depending on the swagger API data type
     *
     * @param dataType the swagger API paramter data type
     * @param paramValue the value of the parameter as string
     * @return the argument
     */
    private Object convertParamToArgument(final DataType dataType, final String paramValue) throws ParseException, IOException {
        switch (dataType) {
            case STRING:
                return paramValue;
            case INTEGER:
                return Integer.parseInt(paramValue);
            case LONG:
                return Long.parseLong(paramValue);
            case BOOLEAN:
                return Boolean.valueOf(paramValue);
            case DATE:
                return new SimpleDateFormat(DATE_FORMAT).parse(paramValue);
            case DATETIME:
                final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
                try {
                    return dateTimeFormat.parse(paramValue);
                } catch (final ParseException ex) {
                    // Swagger UI will encode the URL - let's try with decoded value again
                    return dateTimeFormat.parse(URLDecoder.decode(paramValue, CharEncoding.UTF_8));
                }
            default:
                throw new NotImplementedException("Handling for data type \"" + dataType.name() + "\" not yet implemented.");
        }
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response, HTTPMethod.GET);
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response, HTTPMethod.POST);
    }

    @Override
    protected void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response, HTTPMethod.DELETE);
    }

    @Override
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response, HTTPMethod.PUT);
    }

    @Override
    protected void doOptions(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final SwaggerAPIConfig config = (SwaggerAPIConfig) request.getSession().getServletContext().getAttribute(SwaggerAPIConfig.SERVLET_ATTRIBUTE_NAME);

        if (config.isCrossOriginAccessAllowed()) {
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, UPDATE, OPTIONS");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With");
        }

        if (config.getDefaultContentType() != null) {
            response.setContentType(config.getDefaultContentType());
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
    }
    
    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            super.service(request, response);
        } finally {
            // because Swagger REST calls are stateless the session should be invalidated immediately
            request.getSession().invalidate();
        }
    }
}
