package com.roamsys.swagger.data;

import com.roamsys.swagger.annotations.SwaggerParameter.DataType;
import com.roamsys.swagger.annotations.SwaggerParameter.ParamType;

/**
 * Data class for method parameters
 * @author mbartel
 */
public class SwaggerAPIParameterData {

    /**
     * The data type of the parameter
     */
    private final DataType dataType;

    /**
     * The HTTP parameter type of the parameter
     */
    private final ParamType paramType;

    /**
     * The parameters name
     */
    private final String name;

    /**
     * Creates a new data object for method parameter data
     * @param name the name of the parameter
     * @param paramType the type of parameter
     * @param dataType the data type of the parameter
     */
    public SwaggerAPIParameterData(final String name, final ParamType paramType, final DataType dataType) {
        this.name = name;
        this.paramType = paramType;
        this.dataType = dataType;
    }

    /**
     * Returns the data type of the parameter
     * @return the data type
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Returns the type of parameter
     * @return the parameter type
     */
    public ParamType getParamType() {
        return paramType;
    }

    /**
     * Returns the name of the parameter
     * @return the parameters name
     */
    public String getName() {
        return name;
    }
}
