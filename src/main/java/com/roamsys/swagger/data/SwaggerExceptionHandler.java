package com.roamsys.swagger.data;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
 * Handle exceptions thrown while invoking the Swagger API method.
 *
 * @author mbartel
 */
public interface SwaggerExceptionHandler {

    /**
     * Default implementation that uses {@link System#err}, sets the status and returns error JSON.
     */
    public static final SwaggerExceptionHandler DEFAULT = new SwaggerExceptionHandler() {

        @Override
        public void handleException(final HttpServletResponse response, final int code, final String message, final Throwable ex) {
            System.err.println("Error: " + message + ", status code: " + code);
            if (ex != null) {
                ex.printStackTrace(System.err);
            }
            response.setStatus(code);
            // write error JSON
            try {
                final PrintWriter writer = response.getWriter();
                response.setContentType(ContentType.JSON_UTF8);
                new GsonBuilder().create().toJson(Map.of("code", code, "reason", message), writer);
                writer.flush();
            } catch (final IOException nested) {
                nested.printStackTrace(System.err);
            }
        }
    };


    /**
     * Handles an exception.
     *
     * @param response the response
     * @param code the HTTP response code
     * @param message the exception message
     * @param ex the exception
     */
    public void handleException(final HttpServletResponse response, final int code, final String message, final Throwable ex);

}
