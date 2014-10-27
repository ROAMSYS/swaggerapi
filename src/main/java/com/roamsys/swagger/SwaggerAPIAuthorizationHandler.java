package com.roamsys.swagger;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface for authentication handler
 * @author mbartel
 */
public interface SwaggerAPIAuthorizationHandler {

    /**
     * Handles a Swagger API authentication request
     * @param request the request
     * @param response the response
     * @return true, if the request is authorized
     * @throws ServletException
     * @throws IOException
     */
    public boolean isRequestAuthorized(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException;
}
