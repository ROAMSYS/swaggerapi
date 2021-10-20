# Swagger Java Client

* [Configuration](#configuration)
* [Register the Swagger API components](#register-the-swagger-api-components)
    * [Start Up listener](#start-up-listener)
    * [Servlet for API calls](#servlet-for-api-calls)
* [Example API declaration](#example-api-declaration)
    * [API class annotation](#api-class-annotation)
    * [API method annotations](#api-method-annotations)
    * [Method parameters](#method-parameters)
* [License](#license)

## Configuration

Create a class that extends the **SwaggerAPIListener** class. Here you can do all your configuration using the instance of SwaggerAPIConfig. Use the **registerModel** method to add your API services.
````java
public class SwaggerListener extends SwaggerAPIListener {
    
    @Override
    public void initialize(final SwaggerAPIConfig config) {
        config.allowCrossOriginAccess();
        config.setAPIVersion("0.1.0");
        config.setBasePath("http://example.roamsys.lan:8080/api/");
        config.setSwaggerVersion("1.1");
        config.setDefaultContentType(SwaggerAPIConfig.CONTENT_TYPE_JSON_UTF8);
        config.registerModel(new TestModel());
    }
}
````
## Register the Swagger API components

### Start Up listener
Register the start up listener class you've created in your **web.xml**. This will initiate the Swagger API services at the start up of your application.
````xml
<web-app ...>
  ...
  <listener>
    ...
    <listener-class>com.roamsys.example.gwt.server.swagger.SwaggerListener</listener-class>
  </listener>
</web-app>
````

### Servlet for API calls
Edit your **web.xml** and add the Swagger API servlet. Thats all.
````xml
<web-app ...>
  ...
  <servlet>
    <servlet-name>SwaggerAPI</servlet-name>
    <servlet-class>com.roamsys.swagger.SwaggerAPIServlet</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>SwaggerAPI</servlet-name>
    <url-pattern>/api/*</url-pattern>
  </servlet-mapping>
</web-app>
````

## Example API declaration
````java
@SwaggerModel (path = "/metadata")
public class MetadataAPI implements SwaggerAPIModel {

    @SwaggerApi (
      notes = "Returns a list of all documents",
      method = HTTPMethod.GET,
      path = "/all",
      summary = "Get document list")
    public void all(final SwaggerAPIContext context) throws IOException {
        context.getResponse().getWriter().println("[ { \"name\" : \"document 1\", \"hash\" : \"abc\"}, { \"name\": \"another document\", \"hash\" : \"rrr\"} ]");
    }

    @SwaggerApi (
      notes = "Returns detailed information a specific document",
      method = HTTPMethod.GET,
      path = "/details/{hash}",
      summary = "Get document details")
    public void allForTypeAndFormat(final SwaggerAPIContext context,
            @SwaggerParameter (
              name = "hash",
              description = "The document hash",
              required = true,
              paramType = ParamType.PATH,
              dataType = DataType.STRING
            ) final String hash) {
        if (hash.equals("abc")) {
            context.getResponse().getWriter().println("{ \"name\" : \"document 1\", \"hash\" : \"abc\", , \"size\" : 1232, , \"extension\" : \"odt\"}");
        } else if (hash.equals("rrr")) {
            context.getResponse().getWriter().println("{ \"name\" : \"another document\", \"hash\" : \"rrr\", , \"size\" : 3532, , \"extension\" : \"zip\"}");
        }
    }
}
````
### API class annotation

Each class defining an API has to be annotated with @SwaggerModel(path = "pathToAPIs"). All APIs defined in the class will have that path as prefix. 
The following example defines two APIs. Both APIs will be available thru the base path of your application followed by */metadata*.

### API method annotations

To make an method available as public API annotate it with @SwaggerApi and provide the usual Swagger specifications:

* **notes** - A description for the API
* **method** - The HTTP method GET, PUT, POST, DELETE
* **path** - The URL pattern containing placeholders for parameters
* **summary** - A short description or name for the API

### Method parameters

The parameters used in the URL must annotated with the @SwaggerParameter annotation, which uses the following properties:

* **name** - The name of the parameter, should be the same as the method argument
* **description** - A short description for the parameter
* **required** - Set this to *true* for mandatory parameters, optional parameters should be placed at the end of the URL, if paramType is PATH
* **paramType** - The type/kind of the parameter
     * *PATH* - For parameters placed in a REST-full URL seperated by slashes
     * *QUERY* - For parameters in a query string append to the URL
     * *BODY* - For parameters in a the body (the data) of a PUT or POST request
     * *HEADER* - For parameters in the request header
     * *FORM* - For parameters in a request body encoded with *multipart/form-data*
* **dataType** - The data type of the parameter
     * *STRING*
     * *INTEGER*
     * *LONG* 
     * *BOOLEAN*
     * *DATE*
     * *DATETIME*

## Documentation support

The support for Swagger UI has been changed since Release 5:
* Former versions support the Swagger UI version 1.x (aka resource.json).
* The current version now supports Swagger UI version 2.0 which is also known as *OpenAPI* framework.

The following URL may be used for getting the OpenAPI specification based on the annotation information defined on class and method level:
`https://<host>/<base path>/swagger.json`

The API key has to be specified as URL parameter 'api_key' or as header field 'X-Api-Key'. 

## License

The MIT License (MIT)

Copyright (c) 2014 Roamsys S.A.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
