package com.mariadassou.vj.helloglass.auth;

import java.io.IOException;
import java.util.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;

@PreMatching
public class AuthFilter implements ContainerRequestFilter {
 
	private static final Logger LOG = Logger.getLogger(AuthFilter.class.getSimpleName());

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
    	if(requestContext.getUriInfo().getPath().startsWith("/oauth2callback")){
            LOG.info("Skipping auth check during auth flow");
    	}
		//TODO
    }

}
