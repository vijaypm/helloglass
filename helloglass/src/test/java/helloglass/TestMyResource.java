package helloglass;

import static org.junit.Assert.assertEquals;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mariadassou.vj.helloglass.BasicObject;
import com.mariadassou.vj.helloglass.MyResource;

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
	public void testPostNotify() {
		BasicObject reqObj = new BasicObject();
		reqObj.setAge(80);
		reqObj.setName("Joe");
		Entity<BasicObject> notificationEntity = Entity.entity(reqObj, MediaType.APPLICATION_JSON);
		Response response = target("myresource/postnotify").request().post(notificationEntity); //Here we send POST request
	    assertEquals(200, response.getStatus());
	}
}
