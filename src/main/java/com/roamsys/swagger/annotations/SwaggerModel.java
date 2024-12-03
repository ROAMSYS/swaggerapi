package com.roamsys.swagger.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The swagger API model
 *
 * @author johanna
 */
@Target ({ElementType.TYPE})
@Retention (RetentionPolicy.RUNTIME)
@Inherited
public @interface SwaggerModel {

    /**
     * The base URL pattern to the model
     *
     * @return URL as string
     */
    String path();

    /**
     * The description of the model
     *
     * @return model description as string
     */
    String description() default "";

    /**
     * The result format of the model.
     *
     * @return result format
     */
    String format() default "json";

}
