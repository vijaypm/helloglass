package com.mariadassou.vj.session;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class Session {
	
	public static final String SESSIONID = "JSESSIONID";
	
	//TODO may need to optimize the concurrency level and timeout
	private static Cache<String, Session> sessionMap = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            //.removalListener(new MyRemovalListener()) 
            .concurrencyLevel(4)
            .recordStats()
            .build();
	
	@NotNull
	public static Session getSession(@Nullable String sessionId) {
		Session session  = null;
		if (sessionId != null) {
			session = sessionMap.getIfPresent(sessionId);
		}
		if (session == null) {
			sessionId = UUID.randomUUID().toString().replaceAll("-", "");
			session = new Session(sessionId);
			sessionMap.put(sessionId, session);
		}
		return session;
	}

	public static void clearSession(String sessionId) {
		sessionMap.invalidate(sessionId);
	}
	
	private String userId;
	private String sessionId;
	
	private Session(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public void setUserId(String newUserId) {
		this.userId = newUserId;
	}
	
	public String getUserId() {
		return this.userId;
	}
	
	public String getSessionId() {
		return this.sessionId;
	}
}
