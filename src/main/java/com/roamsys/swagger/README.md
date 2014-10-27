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