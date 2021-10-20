package com.roamsys.swagger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * The swagger API listener to be executed on startup
 *
 * @author johanna
 */
public abstract class SwaggerAPIListener implements ServletContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        final ServletContext servletContext = sce.getServletContext();

        final SwaggerAPIConfig model = new SwaggerAPIConfig(servletContext);
        servletContext.setAttribute(SwaggerAPIConfig.SERVLET_ATTRIBUTE_NAME, model);

        System.out.print("Initializing Swagger API components ... ");
        initialize(model);
        System.out.println("done. => " + model.getApiSpec().toString());
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        sce.getServletContext().removeAttribute(SwaggerAPIConfig.SERVLET_ATTRIBUTE_NAME);
    }

    /**
     * Initializes the swagger API startup definitions
     *
     * @param model the swagger API model
     */
    public abstract void initialize(final SwaggerAPIConfig model);

}
