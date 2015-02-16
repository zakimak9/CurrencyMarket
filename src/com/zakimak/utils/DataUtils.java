package com.zakimak.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class DataUtils {

	private static Logger logger = Logger.getLogger(DataUtils.class.getName());

	public static boolean insertMarketData(JedisPool jedisPool, String msgJson,
			JsonObject jsonObj) {

		Jedis jedis = jedisPool.getResource();

		try {
			Date date = new Date();
			String data = generateCurrencyData(jsonObj);
			jedis.rpush("mapping:" + jsonObj.getString("currencyFrom") + "->"
					+ jsonObj.getString("currencyTo"), data);
			jedis.rpush(
					"originatingCountry:"
							+ jsonObj.getString("originatingCountry"),
					date.getTime() + "");
			jedis.rpush(
					"date:"
							+ jsonObj.getString("timePlaced").split(" ")[0],
					date.getTime() + "");

		} catch (JedisConnectionException e) {
			logger.log(
					Level.SEVERE,
					"POST Error. Could not connect to Redis server: "
							+ e.getMessage());
			if (null != jedis) {
				jedisPool.returnBrokenResource(jedis);
				jedis = null;
			}

			return false;

		} finally {
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}

		return true;
	}

	public static Map<String, List<String>> getCurrencyMappings(
			JedisPool jedisPool, String keyName, String date) {

		date = date.replaceAll("date:", "");
		
		Map<String, List<String>> result = new TreeMap<String, List<String>>();
		Jedis jedis = jedisPool.getResource();

		try {

			Set<String> keys = jedis.keys("mapping:*");

			for (String key : keys) {
				
				if("All".equals(keyName) || key.contains(keyName)){
					System.out.println(key);
					if("All".equals(date)){
						result.put(key.replaceAll("mapping:*", ""),
								jedis.lrange(key, 0, -1));
					}else{
						List<String> lRange = jedis.lrange(key, 0, -1);
						List<String> filteredRows = new ArrayList<String>();
						for(String value: lRange){
							System.out.println("value:" + value);
							System.out.println("date: " + date);
							if(value.contains(date)){
								System.out.println("i was here");
								filteredRows.add(value);
							}else{
								System.out.println("I was not here");
								//lRange.remove(value);
							}
							
						}
						
						result.put(key.replaceAll("mapping:*", ""),
								filteredRows);
					}
				}
			}

		} catch (JedisConnectionException e) {
			logger.log(
					Level.SEVERE,
					"POST Error. Could not connect to Redis server: "
							+ e.getMessage());
			if (null != jedis) {
				jedisPool.returnBrokenResource(jedis);
				jedis = null;
			}

		} finally {
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}

		return result;

	}
	
	public static TreeSet<String> getMappingKeysOnly(
			JedisPool jedisPool) {

		Jedis jedis = jedisPool.getResource();

		try {

			TreeSet<String> keys = new TreeSet<String>(jedis.keys("mapping:*"));
			return keys;
			

		} catch (JedisConnectionException e) {
			logger.log(
					Level.SEVERE,
					"POST Error. Could not connect to Redis server: "
							+ e.getMessage());
			if (null != jedis) {
				jedisPool.returnBrokenResource(jedis);
				jedis = null;
			}

		} finally {
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}

		return null;

	}
	
	public static TreeSet<String> getAllDates(
			JedisPool jedisPool) {

		Jedis jedis = jedisPool.getResource();

		try {

			TreeSet<String> keys = new TreeSet<String>(jedis.keys("date:*"));
			return keys;
			

		} catch (JedisConnectionException e) {
			logger.log(
					Level.SEVERE,
					"POST Error. Could not connect to Redis server: "
							+ e.getMessage());
			if (null != jedis) {
				jedisPool.returnBrokenResource(jedis);
				jedis = null;
			}

		} finally {
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}

		return null;

	}

	public static Map<String, Integer> getOriginRequestCount(JedisPool jedisPool) {

		Map<String, Integer> result = new TreeMap<String, Integer>();
		Jedis jedis = jedisPool.getResource();

		try {
			Set<String> keys = jedis.keys("originatingCountry:*");

			for (String key : keys) {
				result.put(key.replaceAll("originatingCountry:", ""), jedis
						.lrange(key, 0, -1).size());
			}

		} catch (JedisConnectionException e) {
			logger.log(
					Level.SEVERE,
					"POST Error. Could not connect to Redis server: "
							+ e.getMessage());
			if (null != jedis) {
				jedisPool.returnBrokenResource(jedis);
				jedis = null;
			}

		} finally {
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}

		return result;

	}

	public static String generateOriginRequestCountStringForView(
			JedisPool jedisPool) {

		Map<String, Integer> result = getOriginRequestCount(jedisPool);

		StringBuilder sb = new StringBuilder();

		int i = 0;
		for (String key : result.keySet()) {
			sb.append("[");
			sb.append(i);
			sb.append(",");
			sb.append(result.get(key));
			sb.append("]");
			sb.append(",");
			i++;
		}

		return sb.toString();

	}

	public static String generateOriginRequestCountStringForMapView(
			JedisPool jedisPool) {

		Map<String, Integer> result = getOriginRequestCount(jedisPool);

		JsonObjectBuilder builder = Json.createObjectBuilder();

		for (String key : result.keySet()) {

			builder.add(key, result.get(key));

		}
		String data = builder.build().toString();
		System.out.println(data);
		return data;

	}

	public static String generateCountriesLabel(JedisPool jedisPool) {

		Map<String, Integer> result = getOriginRequestCount(jedisPool);

		StringBuilder sb = new StringBuilder();

		int i = 0;
		for (String key : result.keySet()) {
			sb.append("[");
			sb.append(i);
			sb.append(",");
			sb.append("'" + key + "'");
			sb.append("]");
			sb.append(",");
			i++;
		}

		return sb.toString();

	}

	public static String generateCurrencyData(JsonObject jsonObj) {

		return jsonObj.getString("userId") + "," + jsonObj.getInt("amountSell")
				+ "," + jsonObj.getJsonNumber("amountBuy").doubleValue() + ","
				+ jsonObj.getJsonNumber("rate").doubleValue() + ","
				+ jsonObj.getString("timePlaced") + ","
				+ jsonObj.getString("originatingCountry");
	}

	public static boolean checkIfRateExceeded(HttpServletRequest request,
			JedisPool jedisPool) {

		Date time = new Date();
		String ip = Utils.getIPAddress(request);
		String keyname = ip + ":" + time.getTime();
		Jedis jedis = jedisPool.getResource();

		try {

			String current = jedis.get(keyname);

			if(current==null){
				current = "0";
			}
			
			int counter = Integer.parseInt(current);

			if (counter > 10) {
				return true;
			} else {
				System.out.println("before counter:" + jedis.get("counter"));
				jedis.incr("counter");
				System.out.println("after counter:" + jedis.get("counter"));
				jedis.expire("counter", 10);

				return false;
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		} catch (JedisConnectionException e) {
			logger.log(
					Level.SEVERE,
					"POST Error. Could not connect to Redis server: "
							+ e.getMessage());
			if (null != jedis) {
				jedisPool.returnBrokenResource(jedis);
				jedis = null;
			}
			return false;
		} finally {
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}

	}

}
