package com.roamsys.swagger;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interface for authentication handler.
 *
 * @author mbartel
 */
public interface SwaggerAPIAuthorizationHandler {

    /**
     * Handles a Swagger API authentication request.
     *
     * @param request the request
     * @param response the response
     * @return true, if the request is authorized
     * @throws ServletException
     * @throws IOException
     */
    boolean isRequestAuthorized(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException;

    /**
     * Default implementation for resolving API key from request.
     * The following strategy is used:
     * <ol>
     * <li>Check for URL parameter <code>api_key</code></li>
     * <li>Check for header field <code>X-Api-Key</code></li>
     * </ol>
     *
     * @param request the request
     * @return the resolved API key or <code>null</code>
     */
    default String resolveApiKey(final HttpServletRequest request) {
        final String requestAPIKeyParameter = request.getParameter("api_key");
        if (requestAPIKeyParameter == null) {
            return request.getHeader("x-api-key");
         } else {
            return requestAPIKeyParameter;
        }
    }
}
