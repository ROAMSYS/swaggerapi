package com.roamsys.swagger;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface for pre- and post-request handlers
 * @author mbartel
 */
public interface SwaggerAPICustomRequestHandler {

    /**
     * Handles a Swagger API request
     * @param request the request
     * @param response the response
     * @throws ServletException
     * @throws IOException
     */
    public void handle(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException;
}
