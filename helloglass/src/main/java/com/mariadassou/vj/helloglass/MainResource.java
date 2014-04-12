package com.mariadassou.vj.helloglass;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class MainResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String sayHtmlHello() {
    	//TODO use JSP instead of hardcoding html in java
    	// Refer http://blog.usul.org/using-jsp-in-a-jersey-jax-rs-restful-application/
       return "<html> " + "<title>" + "My Jevees Home" + "</title>"
          + "<body><h1>" + "Yes, Sir!" + "</body></h1>" + "</html> ";
    }

}
