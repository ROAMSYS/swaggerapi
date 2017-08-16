package com.roamsys.swagger.data;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

/**
 * Handle exceptions thrown while invoking the Swagger API method.
 *
 * @author mbartel
 */
public interface SwaggerExceptionHandler {

    /**
     * Default implementation that uses {@link System#err}.
     */
    public static final SwaggerExceptionHandler DEFAULT = new SwaggerExceptionHandler() {

        @Override
        public void handleException(final HttpServlet servlet, final HttpServletResponse response, final int code, final String message, final Throwable ex) {
            System.err.println("Error: " + message + ", status code: " + code);
            if (ex != null) {
                ex.printStackTrace(System.err);
            }
        }
    };


    /**
     * Handles an exception.
     *
     * @param servlet the servlet
     * @param response the response
     * @param code the HTTP response code
     * @param message the exception message
     * @param ex the exception
     */
    public void handleException(final HttpServlet servlet, final HttpServletResponse response, final int code, final String message, final Throwable ex);

}
