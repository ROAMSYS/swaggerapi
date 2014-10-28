# Swagger Java Client

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

### Example API declaration

````java
@SwaggerModel (path = "/metadata")
public class MetadataAPI implements SwaggerAPIModel {

    @SwaggerApi (notes = "Returns a list of all documents", method = HTTPMethod.GET, path = "/all", summary = "Get document list")
    public void all(final SwaggerAPIContext context) throws IOException {
        context.getResponse().getWriter().println("[ { \"name\" : \"document 1\", \"hash\" : \"abc\"}, { \"name\": \"another document\", \"hash\" : \"rrr\"} ]");
    }

    @SwaggerApi (notes = "Returns detailed information a specific document", method = HTTPMethod.GET, path = "/details/{hash}", summary = "Get document details")
    public void allForTypeAndFormat(final SwaggerAPIContext context,
            @SwaggerParameter (name = "hash", description = "The document hash", required = true, paramType = ParamType.PATH, dataType = DataType.STRING) final String hash) {
        if (hash.equals("abc")) {
            context.getResponse().getWriter().println("{ \"name\" : \"document 1\", \"hash\" : \"abc\", , \"size\" : 1232, , \"extension\" : \"odt\"}");
        } else if (hash.equals("rrr")) {
            context.getResponse().getWriter().println("{ \"name\" : \"another document\", \"hash\" : \"rrr\", , \"size\" : 3532, , \"extension\" : \"zip\"}");
        }
    }
}

````

# License

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
