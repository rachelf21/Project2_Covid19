package com.databaseproject;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class StatesController
 */
@WebServlet("/StatesController")
public class StatesController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Database db = new Database();
	private DataSource dataSource;


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		System.out.println("States Controller servlet");
		States.createListOfStates(db);
		HashMap<String, String> states = States.getListOfStates();
        for (String url : states.values())  
            System.out.println("value: " + url); 
		request.setAttribute("STATES_LIST", states);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
		dispatcher.forward(request, response);
	}
}
