package com.zakimak.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/testmessage")
public class ClientTest {

	public static String BASE_URL = "http://localhost:8080/CurrencyMarket/rs/messages/save";
	
	@GET
	@Path("/insert")
	public Response testClient() {
		
		Client client = ClientBuilder.newClient();
		WebTarget myResource = client.target(BASE_URL);
		String data = "{\"userId\": \"134256\", \"currencyFrom\": \"BDT\", \"currencyTo\": \"GBP\", \"amountSell\": 333, \"amountBuy\": 455, \"rate\":0.7471, \"timePlaced\" : \"15-JAN-15 10:27:44\", \"originatingCountry\" : \"GB\"}";
		
		try {
			
		  Entity<String> jsonData = Entity.entity(data, MediaType.APPLICATION_JSON);
		  String response = myResource.request(MediaType.APPLICATION_JSON).post(jsonData, String.class);
		  System.out.println(response);
			
		} catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
			e.printStackTrace();
			
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type("text/plain")
					.entity("Something went wrong").build();
		}
		
		return Response.status(Response.Status.OK).type("text/plain")
				.entity("Call was a success").build();

	}

}
