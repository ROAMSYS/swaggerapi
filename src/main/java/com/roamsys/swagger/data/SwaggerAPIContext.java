package com.roamsys.swagger.data;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Context for an swagger API call.
 *
 * @author mbartel
 */
public class SwaggerAPIContext {

    /**
     * The Swagger API request
     */
    private final HttpServletRequest request;
    /**
     * The Swagger API response
     */
    private final HttpServletResponse response;
    /**
     * The Swagger API servlet
     */
    private final HttpServlet servlet;
    /**
     * The Swagger API exception handler
     */
    private final SwaggerExceptionHandler exceptionHandler;

    /**
     * Create a new Swagger API request context.
     *
     * @param servlet the servlet
     * @param request the request
     * @param response the response
     */
    public SwaggerAPIContext(final HttpServlet servlet, final HttpServletRequest request, final HttpServletResponse response, final SwaggerExceptionHandler exceptionHandler) {
        this.servlet = servlet;
        this.request = request;
        this.response = response;
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * The HTTP request.
     *
     * @return the HTTP request
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * The HTTP response.
     *
     * @return the HTTP response
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * The HTTP servlet.
     *
     * @return the servlet
     */
    public HttpServlet getServlet() {
        return servlet;
    }

    /**
     * Gets the exception handler.
     *
     * @return the exception handler
     */
    public SwaggerExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }


}
