package com.mariadassou.vj.helloglass;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
	
	@Path("postnotify")
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
	public Response postNotify(BasicObject reqObj){
		LOG.info("received notification:" + reqObj);
		return Response.status(200).build();
	}
}
