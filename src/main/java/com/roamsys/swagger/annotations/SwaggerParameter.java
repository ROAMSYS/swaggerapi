package com.roamsys.swagger.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The swagger API parameter annotation
 *
 * @author johanna
 */
@Target ({ElementType.PARAMETER})
@Retention (RetentionPolicy.RUNTIME)
public @interface SwaggerParameter {

    /**
     * Enum constants for parameter types
     */
    public enum ParamType {

        PATH("path"),
        QUERY("query"),
        BODY("body"),
        HEADER("header"),
        FORM("form");

        private final String name;

        private ParamType(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * Enum constants for parameter date types
     */
    public enum DataType {
        DATA("data"),
        STRING("string"),
        INTEGER("integer"),
        DATE("date"),
        BOOLEAN("boolean"),
        LONG("long"),
        FLOAT("float"),
        DOUBLE("double"),
        BYTE("byte"),
        DATETIME("dateTime");

        private final String name;

        private DataType(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * The parameter name as it appears in the URL pattern.
     *
     * @return name
     */
    String name();

    /**
     * Defines if parameter is mandatory
     *
     * @return boolean if parameter is required
     */
    boolean required();

    /**
     * Another way to allow multiple values for a "query" parameter. If used, the query parameter may accept comma-separated values.
     *
     * @return true if multiple allowed
     */
    boolean allowMultiple() default false;

    /**
     * The parameter description.
     *
     * @return description as string
     */
    String description();

    /**
     * The type of the parameter (that is, the location of the parameter in the request).
     *
     * @return parameter type (default is PATH)
     */
    ParamType paramType() default ParamType.PATH;

    /**
     * The data type of the parameter
     * @return the data type (default is STRING)
     */
    DataType dataType() default DataType.STRING;
}
