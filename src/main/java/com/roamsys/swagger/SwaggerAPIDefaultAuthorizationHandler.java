package com.roamsys.swagger;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Check for a valid API key
 * @author mbartel
 */
public class SwaggerAPIDefaultAuthorizationHandler implements SwaggerAPIAuthorizationHandler {

    /**
     * The API key
     */
    private final String apiKey;

    /**
     * Creates a new authentication handler that only checks the API key
     * @param apiKey the API key
     */
    public SwaggerAPIDefaultAuthorizationHandler(final String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public boolean isRequestAuthorized(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String requestAPIKeyParam = request.getParameter("api_key");
        return requestAPIKeyParam != null && requestAPIKeyParam.equals(apiKey);
    }

}
