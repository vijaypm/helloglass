package com.mariadassou.vj.simple;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.google.api.services.mirror.model.Notification;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

	  private static final Logger LOG = Logger.getLogger(MyResource.class.getSimpleName());

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
	@Path("getit")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }
	
	@Path("postobject")
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response postObject(BasicObject reqObj){
		LOG.info("received object:" + reqObj);
		return Response.status(200).build();
	}

	@Path("glassnotify")
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response postGlassNotify(
			@Context UriInfo uriInfo, 
			@CookieParam(value = "JSESSIONID") String sessionId,
			Notification notification) {
		//INFO: uri path is: /myresource/glassnotify
		LOG.info("uri path is: " + uriInfo.getPath()); 
		//INFO: uri absolutePath is: http://localhost:9998/myresource/glassnotify
		LOG.info("uri absolutePath is: " + uriInfo.getAbsolutePath());
		//INFO: baseUri is: http://localhost:9998/
		LOG.info("baseUri is: " + uriInfo.getBaseUri());
		//INFO: baseUri path is: /
		LOG.info("baseUri path is: " + uriInfo.getBaseUri().getPath());
		//INFO: baseUri rawPath is: /
		LOG.info("baseUri rawPath is: " + uriInfo.getBaseUri().getRawPath());
		LOG.info("received notification:" + notification);
		if (sessionId == null) {
			sessionId = "123-test-jersey-cookie";
		}
		NewCookie sessionCookie = new NewCookie("JSESSIONID", sessionId);
		return Response.status(200).cookie(sessionCookie).build();
	}
	
    @Path("item/{item_id}")  
    @GET  
    public Response getItem(
			@Context UriInfo uriInfo, 
			@PathParam("item_id") String itemId) {  
        return Response.temporaryRedirect(  
            UriBuilder.fromUri(uriInfo.getRequestUri()).queryParam("upc",itemId).build()
            ).build();  
    } 

}
