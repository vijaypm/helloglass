package com.mariadassou.vj.helloglass;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.model.Location;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.Notification;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.api.services.mirror.model.UserAction;
import com.google.common.collect.Lists;
import com.mariadassou.vj.helloglass.auth.AuthResource;
import com.mariadassou.vj.helloglass.auth.AuthUtil;

@Path("notify")
public class NotifyResource {
	private static final Logger LOG = LoggerFactory.getLogger(AuthResource.class);
	  
		@Path("glassnotify")
		@POST
	    @Consumes(MediaType.APPLICATION_JSON)
		public Response postGlassNotify(
				@Context UriInfo uriInfo,
				Notification notification) throws IOException{
			LOG.info("received notification:" + notification);
		    String userId = notification.getUserToken();
		    Credential credential = AuthUtil.getCredential(userId);
		    Mirror glass = MirrorClient.getMirror(credential);

		    if (notification.getCollection().equals("locations")) {
		    	LOG.info("Notification of updated location");
		    	// item id is usually 'latest'
		    	try {
		    		Location location = glass.locations().get(notification.getItemId()).execute();

		    		LOG.info("New location is " + location.getLatitude() + ", " + location.getLongitude());
		    		// TODO Put location into a queue to be analyzed
		    		// TODO Analyze history and provide feedback iff required.
		    		MirrorClient.insertTimelineItem(
		    				credential,
		    				new TimelineItem()
		    				.setText("My Jeeves says you are now at " + location.getLatitude()
		    						+ " by " + location.getLongitude())
		    						.setNotification(new NotificationConfig().setLevel("DEFAULT")).setLocation(location)
		    						.setMenuItems(Lists.newArrayList(new MenuItem().setAction("NAVIGATE"))));
		    	} catch (GoogleJsonResponseException e) {
		    		LOG.error("Error interacting with Glass:", e);
		    		if (e.getStatusCode() > 400 && e.getStatusCode() < 500) {
		    			LOG.error("Authentication issue. Restarting OAuth dance");
		    			AuthUtil.clearUserId(userId);
		    			Response.temporaryRedirect(
		    					UriBuilder.fromUri(uriInfo.getBaseUri().toString() + "oauth2callback").build()
		    					).build();
		    		} else {
		    			LOG.error("Temporary issue. Ignoring.");
		    		}
		    	}

		    } else if (notification.getUserActions().contains(new UserAction().setType("LAUNCH"))) {
		          LOG.info("Feedback provided with the 'record feedback' voice command. Processing it.");
		      } else {
		          LOG.warn("I don't know what to do with this notification, so I'm ignoring it.");
		        }
		    
			return Response.status(200).build();
		}



}
