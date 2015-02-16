package com.zakimak.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zakimak.utils.DataUtils;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * Servlet implementation class Display
 */
@WebServlet("/display")
public class Display extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static JedisPool pool;
	private static Logger logger = Logger.getLogger(Display.class.getName());
	
	static {
		try {
			pool = new JedisPool(new JedisPoolConfig(), "localhost",
					Protocol.DEFAULT_PORT, Protocol.DEFAULT_TIMEOUT);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to configure redis");
		}
	}
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Display() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String currencyWatch = request.getParameter("currencyWatch");
		String dateWatch = request.getParameter("dateWatch");
		
		if(currencyWatch==null){
			currencyWatch = "All";
		}
		
		if(dateWatch==null){
			dateWatch = "All";
		}
		
		TreeSet<String> allKeys = DataUtils.getMappingKeysOnly(pool);
		request.setAttribute("allKeys", allKeys);
		
		TreeSet<String> allDates = DataUtils.getAllDates(pool);
		request.setAttribute("allDates", allDates);
		
		Map<String, List<String>> keys = DataUtils.getCurrencyMappings(pool, currencyWatch, dateWatch);
		request.setAttribute("keys", keys);
		
		String originRequestCountStringForView = DataUtils.generateOriginRequestCountStringForView(pool);
		request.setAttribute("originRequestCountStringForView", originRequestCountStringForView);
		
		String originRequestCountStringForMapView = DataUtils.generateOriginRequestCountStringForMapView(pool);
		request.setAttribute("originRequestCountStringForMapView", originRequestCountStringForMapView);
		System.out.println(originRequestCountStringForMapView);
		
		String countriesLabel = DataUtils.generateCountriesLabel(pool);
		request.setAttribute("countriesLabel", countriesLabel);
		
		System.out.println("requestParam: " + request.getParameter("currencyWatch"));
		
		request.getRequestDispatcher("/display.jsp").forward(request, response);
		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
