package com.databaseproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.postgresql.PGConnection;

/**
 * Servlet implementation class ResultControllerServlet
 */
@WebServlet("/ResultControllerServlet")
public class ResultControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private ResultDbUtil resultDbUtil;

	// Resource(name = "jdbc/covid") //for tomcat server
	@Resource(name = "jdbc/postgres") // for amazon server
	// driverClassName="org.postgresql.ds.PGPoolingDataSource" /for amazon server

	private DataSource dataSource;
	String address = "https://covidtracking.com/api/v1/states/daily.csv";
	String tableWithLatestData = "coviddata3";
	// String path = "C:\\Users\\Rachel\\eclipse-workspace\\Covid19\\data\\";
	// String filename = path + "data.csv";

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();

		// create our resultdbutil... and pass in the conn pool / datasource

		try {
			resultDbUtil = new ResultDbUtil(dataSource);
		}

		catch (Exception e) {
			System.err.println("init error");
			throw new ServletException(e);
		}

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {

			String theCommand = request.getParameter("command");

			if (theCommand == null) {
				theCommand = "LIST_STATES";
			}

			switch (theCommand) {

			case "LIST_STATES":
				createStates(request, response);
//				request.setAttribute("STATES_LIST", states);
//				RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
//				dispatcher.forward(request, response);
				downloadData(request, response);
				break;

			case "CREATE":
				createTable2(request, response);
				break;

			case "LIST":
				// list results in MVC fashion
				// get data, set attribute, use request dispatcher and send it to jsp page
				listResults(request, response);
				break;

			case "ADD":
				addResult(request, response);
				break;

			case "LOAD":
				loadResult(request, response);
				break;

			case "UPDATE":
				updateResult(request, response);
				break;

			case "DELETE":
				deleteResult(request, response);
				break;

			default:
				listResults(request, response);
			}
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
	}

	// downloads data and creates 3 tables based on the data. Includes all states
	// and dates
	private void downloadData(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException {
		WebScraper ws = new WebScraper();
		byte[] content = ws.retrieveDataFromWebsite(address);
		ServletContext context = request.getServletContext();
		String path = context.getRealPath("/");
		String filename = path + "data.csv";
		System.out.println("filename = " + filename);
		String columns = ws.saveToFile(filename, content);

		try {
			Database db = new Database();
			Connection conn = null;
			conn = dataSource.getConnection();
			db.convertToTable(conn, tableWithLatestData, columns);
			PGConnection pgConnection = conn.unwrap(PGConnection.class);
			db.addRecords(pgConnection, filename, tableWithLatestData);

			db.createTable(conn, tableWithLatestData, "positive", "date, state, positive");
			db.createTable(conn, tableWithLatestData, "hospitalizations", "date, state, hospitalizedcumulative");
			db.createTable(conn, tableWithLatestData, "death", "date, state, death");

		}
		catch (Exception e) {
			System.err.println("CREATE CLAUSE");
			e.printStackTrace();
		}
	}

	private void createStates(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException {
		// get results from resultsdbutil

//		ServletContext context = request.getServletContext();
//		WebScraper ws = new WebScraper();
//		byte[] content = ws.retrieveDataFromWebsite(address);
//		// String path = "C:\\Users\\Rachel\\eclipse-workspace\\Covid19\\data\\";
//		String path = context.getRealPath("/");
//		System.out.println("getRealPath = " + path);
//
//		String filename = path + "data.csv";
//		String columns = ws.saveToFile(filename, content);
//		System.out.println("Columns: " + columns);

		// create state lists. remove everything on top if this doesnt work

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = null;
			stmt = null;
			rs = null;
			conn = dataSource.getConnection();
			PGConnection pgConnection = conn.unwrap(PGConnection.class);

			ServletContext context = request.getServletContext();
			String path = context.getRealPath("/");
			Database db = new Database();
			db.convertToTable(conn, "states", "id integer primary key, ST text, state text");
			db.addRecords(pgConnection, path + "states.csv", "states");
		}
		finally {
			LinkedHashMap<String, String> states = resultDbUtil.getListOfStates();

			// add results to the request
			request.setAttribute("STATES_ABBR_LIST", states);

			// send to JSP page (view)
			RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
			dispatcher.forward(request, response);
			if (stmt != null)
				stmt.close();
			if (rs != null)
				rs.close();
			if (conn != null)
				conn.close();
		}
	}

	private void createTable(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String mmddB = request.getParameter("start_date");
		mmddB = mmddB.substring(5);
		String mmddA = request.getParameter("end_date");
		mmddA = mmddA.substring(5);
		Database db = new Database();
		WebScraper ws = new WebScraper();
		byte[] content = ws.retrieveDataFromWebsite(address);
		// String path = "C:\\Users\\Rachel\\eclipse-workspace\\Covid19\\data\\";
		ServletContext context = request.getServletContext();
		String path = context.getRealPath("/");
		String filename = path + "data.csv";
		System.out.println("filename = " + filename);
		String columns = ws.saveToFile(filename, content);

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = null;
			rs = null;
			conn = dataSource.getConnection();
			db.convertToTable(conn, tableWithLatestData, columns);
			PGConnection pgConnection = conn.unwrap(PGConnection.class);
			db.addRecords(pgConnection, filename, tableWithLatestData);

			db.createTable(conn, tableWithLatestData, "positive", "date, state, positive");
			db.createTable(conn, tableWithLatestData, "hospitalizations", "date, state, hospitalizedcumulative");
			db.createTable(conn, tableWithLatestData, "death", "date, state, death");
			db.dropTable(conn, "results");
			String state = request.getParameter("state");
			db.selectByStateNoOutput(conn, state);
			db.deleteOldRecords(conn, "results", mmddB);
			db.deleteRecordsAfter(conn, "results", mmddA);
			listResults(request, response);

		}
		catch (Exception e) {
			System.err.println("CREATE CLAUSE");
			e.printStackTrace();
		}
		finally {
			if (conn != null)
				conn.close();
			if (stmt != null)
				stmt.close();
			if (rs != null)
				rs.close();
		}
	}

	// replaced the createTable which I will delete eventually. This one put the
	// download into a separate method
	private void createTable2(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String mmddB = request.getParameter("start_date");
		mmddB = mmddB.substring(5);
		String mmddA = request.getParameter("end_date");
		mmddA = mmddA.substring(5);
		Database db = new Database();

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			Database.dropTable(conn, "results");
			String state = request.getParameter("state");
			db.selectByStateNoOutput(conn, state);
			db.deleteOldRecords(conn, "results", mmddB);
			db.deleteRecordsAfter(conn, "results", mmddA);
			listResults(request, response);

		}
		catch (Exception e) {
			System.err.println("CREATE CLAUSE");
			e.printStackTrace();
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}

	private void deleteResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int id = Integer.parseInt(request.getParameter("resultID"));
		resultDbUtil.deleteResult(id);
		listResults(request, response);
	}

	private void updateResult(HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {
			// read result info from form data
			int id = Integer.parseInt(request.getParameter("resultId"));
			String dateString = request.getParameter("date");
			System.out.println("dateString =" + dateString);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			Date date = new Date();
			try {
				date = formatter.parse(dateString);
				System.out.println("Date is now " + date);
			}
			catch (ParseException e) {
				System.err.println("Error formatting date");
				e.printStackTrace();
			}
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			String state = request.getParameter("state");
			int pos = Integer.parseInt(request.getParameter("positive"));
			int hosp = Integer.parseInt(request.getParameter("hosp"));
			int death = Integer.parseInt(request.getParameter("death"));

			// create a new result object
			Result theResult = new Result(id, sqlDate, state, pos, hosp, death);

			// perform update on database
			resultDbUtil.updateResult(theResult);

			// send user back to list page
			listResults(request, response);
		}
		catch (NumberFormatException e) {
			System.out.println("One or more fields cannot be blank");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/error-update.jsp");
			dispatcher.forward(request, response);
		}
	}

	private void loadResult(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// read resultid from form data
		String id = request.getParameter("resultID");

		// get result from dbutil
		Result result = resultDbUtil.getResult(id);

		// place request in request attribute
		request.setAttribute("THE_RESULT", result);

		// send to jsp page: update-Result
		RequestDispatcher dispatcher = request.getRequestDispatcher("/update-entry-form2.jsp");
		dispatcher.forward(request, response);
	}

	private void addResult(HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {
			// read Result info from form data
			String dateString = request.getParameter("date");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			Date date = new Date();
			try {
				date = formatter.parse(dateString);
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			String state = request.getParameter("state");
			int pos = Integer.parseInt(request.getParameter("positive"));
			int hosp = Integer.parseInt(request.getParameter("hosp"));
			int death = Integer.parseInt(request.getParameter("death"));

			// create a new result object
			Result result = new Result(sqlDate, state, pos, hosp, death);

			// add the Result to the database
			resultDbUtil.addResult(result);
			// send back to main page (the list)
			listResults(request, response);
		}
		catch (NumberFormatException e) {
			System.out.println("One or more fields cannot be blank");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
			dispatcher.forward(request, response);
		}
		finally {
			// send back to main page (the list)
			// listResults(request, response);
		}
	}

	private void listResults(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// get results from resultsdbutil
		List<Result> results = resultDbUtil.getResults();

		// add results to the request
		request.setAttribute("RESULT_LIST", results);

		// send to JSP page (view)
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-results.jsp");
		dispatcher.forward(request, response);

	}

}
