package com.zakimak.service;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.zakimak.utils.DataUtils;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

@Stateless
@Path("/messages")
public class MessageConsumer {
	
	private static JedisPool pool;
	private static Logger logger = Logger.getLogger(MessageConsumer.class.getName());
	
	static {
		try {
			pool = new JedisPool(new JedisPoolConfig(), "localhost",
					Protocol.DEFAULT_PORT, Protocol.DEFAULT_TIMEOUT);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to configure redis");
		}
	}
	
	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveMessage(String msgJson, @Context HttpServletRequest req) {

		if (msgJson == null || msgJson.isEmpty()) {

			return Response.status(Response.Status.BAD_REQUEST).type("text/plain")
					.entity("You have not provided any data or data is missing in your request").build();
		} 
		
		if(DataUtils.checkIfRateExceeded(req, pool)){
			return Response.status(Response.Status.BAD_REQUEST).type("text/plain")
					.entity("Too many requests in a second. Try again shortly.").build();
		}
		
		StringReader reader = new StringReader(msgJson);
		JsonObject jsonObject = Json.createReader(reader).readObject();
		
		boolean success = DataUtils.insertMarketData(pool, msgJson, jsonObject);
		
		if(!success){
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type("text/plain")
					.entity("Failed to connected to Redis.").build();
		}
		
		return Response.status(Response.Status.CREATED).type("text/plain")
				.entity("Successfully saved your message to database.").build();
	}

}
