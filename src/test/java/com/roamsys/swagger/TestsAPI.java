package com.roamsys.swagger;

import com.roamsys.swagger.annotations.SwaggerApi;
import com.roamsys.swagger.annotations.SwaggerApi.HTTPMethod;
import com.roamsys.swagger.annotations.SwaggerModel;
import com.roamsys.swagger.annotations.SwaggerParameter;
import com.roamsys.swagger.annotations.SwaggerParameter.DataType;
import com.roamsys.swagger.annotations.SwaggerParameter.ParamType;
import com.roamsys.swagger.data.SwaggerAPIContext;

@SwaggerModel(path = "/testAPI")
public class TestsAPI implements SwaggerAPIModel {
    public static final String PATH_TEST = "/test";
    public static final String PATH_TEST_ORGANISATION = "/test/{organisationidList}";
    public static final String PATH_TEST_ORGANISATION_BEFORE_DATE = "/test/{organisationidList}/before/{date}";
    public static final String PATH_TEST_ORGANISATION_AFTER_DATE = "/test/{organisationidList}/after/{date}";
    public static final String PATH_TEST_ORGANISATION_TADIG = "/test/{organisationidList}/{tadigList}";

    @SwaggerApi(method = SwaggerApi.HTTPMethod.GET, path = PATH_TEST_ORGANISATION, summary = "", notes = "")
    public void test_Org(final SwaggerAPIContext context,
            @SwaggerParameter(name = "organisationidList", description = "", required = true, paramType = ParamType.PATH, dataType = DataType.STRING) final String organisationidList) {
        //Do nothing
    }

    @SwaggerApi(method = SwaggerApi.HTTPMethod.GET, path = PATH_TEST, summary = "", notes = "")
    public void test(final SwaggerAPIContext context) {
        //Do nothing
    }

    @SwaggerApi(notes = "", method = HTTPMethod.GET, path = PATH_TEST_ORGANISATION_TADIG, summary = "")
    public void test_Org_Tadig(final SwaggerAPIContext context,
            @SwaggerParameter(name = "organisationidList", description = "", required = true, paramType = ParamType.PATH, dataType = DataType.STRING) final String organisationidList,
            @SwaggerParameter(name = "tadigList", description = "", required = true, paramType = ParamType.PATH, dataType = DataType.STRING) final String tadigList) {
        //Do nothing
    }

    @SwaggerApi(notes = "", method = HTTPMethod.GET, path = PATH_TEST_ORGANISATION_AFTER_DATE, summary = "")
    public void test_Org_After_Date(final SwaggerAPIContext context,
            @SwaggerParameter(name = "organisationidList", description = "", required = true, paramType = ParamType.PATH, dataType = DataType.STRING) final String organisationidList,
            @SwaggerParameter(name = "date", description = "", required = true, paramType = ParamType.PATH, dataType = DataType.STRING) final String date) {
        //Do nothing
    }

    @SwaggerApi(notes = "", method = HTTPMethod.GET, path = PATH_TEST_ORGANISATION_BEFORE_DATE, summary = "")
    public void test_Org_Before_Date(final SwaggerAPIContext context,
            @SwaggerParameter(name = "organisationidList", description = "", required = true, paramType = ParamType.PATH, dataType = DataType.STRING) final String organisationidList,
            @SwaggerParameter(name = "date", description = "", required = true, paramType = ParamType.PATH, dataType = DataType.STRING) final String date) {
        //Do nothing
    }
}