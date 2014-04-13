package com.mariadassou.vj.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.api.services.mirror.model.Notification;
import com.mariadassou.vj.simple.BasicObject;
import com.mariadassou.vj.simple.MyResource;

public class TestMyResource extends JerseyTest {
	
	@BeforeClass
	public static void beforClass() {
		LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.INFO);
	}
	
	@Override
	protected Application configure() {
		return new ResourceConfig(MyResource.class);
	}

	@Test
	public void testGetIt() {
		final String hello = target("myresource/getit").request().get(String.class);
		assertEquals("Got it!", hello);
	}

	@Test
	public void testPostObject() {
		BasicObject reqObj = new BasicObject();
		reqObj.setAge(80);
		reqObj.setName("Joe");
		Entity<BasicObject> objectEntity = Entity.entity(reqObj, MediaType.APPLICATION_JSON);
		Response response = target("myresource/postobject").request().post(objectEntity); //Here we send POST request
	    assertEquals(200, response.getStatus());
	}
	
	@Test
	public void testPostNotify_newCookie() {
		Notification notification = new Notification();
		notification.setItemId("123");
		notification.setCollection("timeline");
		Entity<Notification> notificationEntity = Entity.entity(notification, MediaType.APPLICATION_JSON);
		Response response = target("myresource/glassnotify").request().post(notificationEntity); //Here we send POST request
	    assertEquals(200, response.getStatus());
	    Map<String, NewCookie> cookies = response.getCookies();
	    assertEquals(1, cookies.size());
	    NewCookie sessionCookie = cookies.get("JSESSIONID");
	    assertNotNull(sessionCookie);
	    assertEquals("123-test-jersey-cookie", sessionCookie.getValue());
	}

	@Test
	public void testPostNotify_returnCookie() {
		Notification notification = new Notification();
		notification.setItemId("123");
		notification.setCollection("timeline");
		Entity<Notification> notificationEntity = Entity.entity(notification, MediaType.APPLICATION_JSON);
		NewCookie sessionCookie = new NewCookie("JSESSIONID", "abc-test-jersey-cookie");
		Response response = target("myresource/glassnotify").request().cookie(sessionCookie).post(notificationEntity); //Here we send POST request
	    assertEquals(200, response.getStatus());
	    Map<String, NewCookie> cookies = response.getCookies();
	    assertEquals(1, cookies.size());
	    sessionCookie = cookies.get("JSESSIONID");
	    assertNotNull(sessionCookie);
	    assertEquals("abc-test-jersey-cookie", sessionCookie.getValue());
	}
	
	@Test
	public void testGetItem() {
		WebTarget target = target("myresource/item/12345");
		target.property(ClientProperties.FOLLOW_REDIRECTS, false);
		Invocation invocation = target.request().buildGet();
		Response response = invocation.invoke();
	    assertEquals(307, response.getStatus());
	    Map<String, NewCookie> cookies = response.getCookies();
	    assertEquals(1, cookies.size());
	    Cookie sessionCookie = cookies.get("JSESSIONID");
	    assertNotNull(sessionCookie);
	    assertEquals("123-test-jersey-item-cookie", sessionCookie.getValue());
	    assertEquals("http://localhost:9998/myresource/item/12345?upc=12345", response.getLocation().toString());
	}
}
