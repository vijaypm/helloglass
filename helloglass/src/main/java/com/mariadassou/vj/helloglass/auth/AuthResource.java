package com.mariadassou.vj.helloglass.auth;

import java.io.IOException;
import java.util.logging.Logger;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.mariadassou.vj.session.Session;

/**
 * Root resource (exposed at "oauth2callback" path)
 */
@Path("oauth2callback")
public class AuthResource {

	private static final Logger LOG = Logger.getLogger(AuthResource.class.getSimpleName());
	
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response doGet(
    		@CookieParam(value = "JSESSIONID") String sessionId,
    		@QueryParam("error") String error,
    		@QueryParam("code") String code,
    		@Context UriInfo uriInfo) throws IOException {

        // If something went wrong, log the error message.
    	if (error != null) {
    	      LOG.severe("Something went wrong during auth: " + error);
    	      String result = "Something went wrong during auth: " + error;
    	      return Response.status(Status.OK).entity(result).build();
    	}
    	
        // If we have a code, finish the OAuth 2.0 dance
    	if (code != null) {
    	      LOG.info("Got a code. Attempting to exchange for access token.");
    	      AuthorizationCodeFlow flow = AuthUtil.newAuthorizationCodeFlow();
    	      TokenResponse tokenResponse =
    	              flow.newTokenRequest(code)
    	                  .setRedirectUri(uriInfo.getAbsolutePath().toString()).execute();
    	      // Extract the Google User ID from the ID token in the auth response
    	      String userId = ((GoogleTokenResponse) tokenResponse).parseIdToken().getPayload().getUserId();

    	      LOG.info("Code exchange worked. User " + userId + " logged in.");

    	      //Set user into session
    	      Session session = Session.getSession(sessionId);
    	      session.setUserId(userId);
    	      NewCookie sessionCookie = new NewCookie("JSESSIONID", session.getSessionId());
    	      
    	      flow.createAndStoreCredential(tokenResponse, userId);

    	      // The dance is done. Do our bootstrapping stuff for this user
    	      NewUserBootstrapper.bootstrapNewUser(uriInfo.getBaseUri(), userId);

    	      // Redirect back to index
    	      return Response.temporaryRedirect(uriInfo.getBaseUri()).cookie(sessionCookie).build();
    	}
        // Else, we have a new flow. Initiate a new flow.
        LOG.info("No auth context found. Kicking off a new auth flow.");

        AuthorizationCodeFlow flow = AuthUtil.newAuthorizationCodeFlow();
        GenericUrl url =
            flow.newAuthorizationUrl().setRedirectUri(uriInfo.getAbsolutePath().toString());
        url.set("approval_prompt", "force");
        String urlString = url.build();

        return Response.temporaryRedirect(
        		UriBuilder.fromUri(urlString).build()
        		).build();
    }
	
}
