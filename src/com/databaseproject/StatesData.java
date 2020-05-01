package com.databaseproject;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;

/**
 * Servlet implementation class StatesData
 */
@WebListener
public class StatesData implements ServletContextListener {

	private static final String[] states = { "NY", "NJ", "CT", "ME" };

	@Override
	public void contextInitialized(ServletContextEvent event) {
		event.getServletContext().setAttribute("STATES_LIST", states);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// NOOP.
	}

}
