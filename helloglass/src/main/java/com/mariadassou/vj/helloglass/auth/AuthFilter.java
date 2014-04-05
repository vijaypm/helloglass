package com.mariadassou.vj.helloglass.auth;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;

@PreMatching
public class AuthFilter implements ContainerRequestFilter {
 
    @Override
    public void filter(ContainerRequestContext requestContext)
                    throws IOException {
    	//TODO
    }

}
