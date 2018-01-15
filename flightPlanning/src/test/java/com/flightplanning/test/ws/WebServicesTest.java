package com.flightplanning.test.ws;

import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.Entity;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.Assert;


public class WebServicesTest {

	@Test
	public void test(){
		Client client = ClientBuilder.newClient(new ClientConfig().register(JacksonFeature.class));
		WebTarget target = client.target("http://localhost:8081/ws/flights");
		Response rs = target.request().post(Entity.json(null), Response.class);
		Assert.assertEquals(401, rs.getStatus());
	}
	
}
