package com.roamsys.swagger.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The swagger API annotation
 *
 * @author johanna
 */
@Target ({ElementType.METHOD})
@Retention (RetentionPolicy.RUNTIME)
public @interface SwaggerApi {

    /**
     * Enum constants for HTTP methods
     */
    public enum HTTPMethod {

        GET,
        POST,
        PUT,
        DELETE;
    }

    /**
     * The HTTP method of the current service
     *
     * @return HTTP
     */
    HTTPMethod method() default HTTPMethod.GET;

    /**
     * The URL pattern with placeholder for parameters
     *
     * @return URL as string
     */
    String path();

    /**
     * The summary of the current service
     *
     * @return summary as string
     */
    String summary();

    /**
     * A verbose explanation of the operation behavior.
     *
     * @return implementation notes as string
     */
    String notes();    
    
    /**
     * The description for the implementation notes
     *
     * @return description as string
     */
    String description() default "";
}
