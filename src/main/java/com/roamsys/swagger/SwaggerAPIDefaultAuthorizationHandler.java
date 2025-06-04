package com.roamsys.swagger;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
        final String requestAPIKey = resolveApiKey(request);
        return requestAPIKey != null && requestAPIKey.equals(apiKey);
    }

}
