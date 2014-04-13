package com.mariadassou.vj.helloglass.auth;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mariadassou.vj.session.Session;

@Provider
@PreMatching
public class AuthFilter implements ContainerRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(AuthFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		LOG.info("Requested URI:" + requestContext.getUriInfo().getPath());
		if(requestContext.getUriInfo().getPath().startsWith("/oauth2callback")){
			LOG.info("Skipping auth check during auth flow");
			return;
		}
		LOG.debug("*****SECURITY WARNING !!!!!");
		//TODO clean up these log messages to not print sensitive information
		LOG.debug("Checking to see if anyone is logged in");
		Cookie cookie = requestContext.getCookies().get(Session.SESSIONID);
		LOG.debug("cookie:" + cookie);
		if (cookie != null) {
			LOG.debug("cookie value:" + cookie.getValue());
			if (cookie.getValue() != null) {
				LOG.debug("userId:" + AuthUtil.getUserId(cookie.getValue()));
				if (AuthUtil.getUserId(cookie.getValue()) != null) {
					LOG.debug("user credential:" + AuthUtil.getCredential(AuthUtil.getUserId(cookie.getValue())));
					if (AuthUtil.getCredential(AuthUtil.getUserId(cookie.getValue())) != null) {
						LOG.debug("user access token:" + AuthUtil.getCredential(AuthUtil.getUserId(cookie.getValue())).getAccessToken());
					}
				}
			}
		}
		
		if (cookie == null ||
				AuthUtil.getUserId(cookie.getValue()) == null
				|| AuthUtil.getCredential(AuthUtil.getUserId(cookie.getValue())) == null
				|| AuthUtil.getCredential(AuthUtil.getUserId(cookie.getValue())).getAccessToken() == null) {
			// redirect to auth flow
			String redirectUrl = requestContext.getUriInfo().getBaseUri().toString() + "oauth2callback";
			URI redirectUri = UriBuilder.fromUri(redirectUrl).build();
			requestContext.abortWith(Response.temporaryRedirect(redirectUri).build());
			return;
		}
	}

}
