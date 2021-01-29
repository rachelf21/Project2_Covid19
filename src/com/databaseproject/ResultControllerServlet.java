package com.databaseproject;

import java.io.File;
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

	//@Resource(name = "jdbc/covid2") // for local tomcat server
	@Resource(name = "jdbc/ddgha774rb1b8u") // for heroku server
	//@Resource(name = "jdbc/postgres") // for amazon server - switch to context-aws file and change name
	// driverClassName="org.postgresql.ds.PGPoolingDataSource" /for amazon server

	private DataSource dataSource;
	String address = "https://covidtracking.com/api/v1/states/daily.csv";
	String tableWithLatestData = "coviddata3";

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();

		// create our resultdbutil... and pass in the conn pool / datasource

		try {
			resultDbUtil = new ResultDbUtil(dataSource);
			System.out.println("successfully created new resultDbUtil using dataSource");
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
				downloadData(request, response);
				break;

			case "CREATE":
				createTable2(request, response);
				break;

			case "LIST":
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

			case "RESET":
				createStates(request, response);
				downloadData(request, response);
				//resetIndex(request, response);  //have to redownload the data because deleting tables due to limited row space on heroku
				break;

			case "EXIT":
				exitProgram(request, response);
				break;

			default:
				listResults(request, response);
			}
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
	}

	/**
	 * Sends user back to the Index page to allow user to select different state,
	 * starting date, and ending date
	 * 
	 * @param request information sent to servlet
	 * @param response information returned from servlet
	 * @throws ServletException when a servlet error occurs
	 * @throws IOException when the connection stream is interrupted
	 */
	private void resetIndex(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("in resetIndex");
		LinkedHashMap<String, String> states = resultDbUtil.getListOfStates();
		request.setAttribute("STATES_ABBR_LIST", states);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * Closes the database connection and exits the program. This method is called
	 * when the user clicks on the Exit button.
	 * 
	 * @param request information sent to servlet
	 * @param response information returned from servlet
	 * @throws ServletException when a servlet error occurs
	 * @throws IOException when the connection stream is interrupted
	 * @throws SQLException when a database error occurs
	 */
	private void exitProgram(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException {
		resultDbUtil.exit();
		RequestDispatcher dispatcher = request.getRequestDispatcher("/start.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * Downloads the data from covidtracking.com and creates 3 tables (positive
	 * cases, hospitalizations, deaths) based on the data. Each table includes all
	 * states and all dates. This method is called when the user clicks on "Click to
	 * Launch" on the starting page (start.jsp)
	 * 
	 * @param request information sent to servlet
	 * @param response information returned from servlet
	 * @throws ServletException when a servlet error occurs
	 * @throws IOException when the connection stream is interrupted
	 * @throws SQLException when a database error occurs
	 */
	private void downloadData(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException {
		WebScraper ws = new WebScraper();
		byte[] content = ws.retrieveDataFromWebsite(address);
		ServletContext context = request.getServletContext();
		//String path = context.getRealPath("/");
		String path = "/tmp/";
		String filename = path + "data.csv";
		System.out.println("filename = " + filename);
		String columns = ws.saveToFile(filename, content);
		System.out.println(columns);
		Connection conn = null;
		PGConnection pgConnection = null;

		try {
			Database db = new Database();
			conn = dataSource.getConnection();
			db.convertToTable(conn, tableWithLatestData, columns);
			pgConnection = conn.unwrap(PGConnection.class);
			db.addRecords(pgConnection, filename, tableWithLatestData);

			db.createTable(conn, tableWithLatestData, "positive", "date, state, positive");
			db.createTable(conn, tableWithLatestData, "hospitalizations", "date, state, hospitalizedcumulative");
			db.createTable(conn, tableWithLatestData, "death", "date, state, death");

		}
		catch (Exception e) {
			System.err.println("CREATE CLAUSE");
			e.printStackTrace();
		}

		finally {
			if (conn != null)
				conn.close();
			if (pgConnection != null)
				((Connection) pgConnection).close();
		}
	}

	/**
	 * Creates a table for fifty states with abbreviations for each. This table is
	 * then used to populate the State dropdown list on the index page. This method
	 * is called when the user clicks on "Click to Launch"
	 * 
	 * @param request information sent to servlet
	 * @param response information returned from servlet
	 * @throws ServletException when a servlet error occurs
	 * @throws IOException when the connection stream is interrupted
	 * @throws SQLException when a database error occurs
	 */
	private void createStates(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		PGConnection pgConnection = null;

		try {
			conn = null;
			stmt = null;
			rs = null;
			conn = dataSource.getConnection();
			pgConnection = conn.unwrap(PGConnection.class);

			ServletContext context = request.getServletContext();
			//String path = context.getRealPath("/");
			String path = "/tmp/";
			System.out.println("Creating new database");
			Database db = new Database();
			System.out.println("Created new database");
			System.out.println("calling db.convertToTable");			
			db.convertToTable(conn, "states", "id integer primary key, ST text, state text");
			System.out.println("called db.addRecords");			
			System.out.println("calling db.addRecords");			
			db.addRecords(pgConnection, path + "states.csv", "states");
			System.out.println("called db.addRecords");			

		}
		finally {
			System.out.println("in createStates");
			LinkedHashMap<String, String> states = resultDbUtil.getListOfStates();
			// add results to the request
			request.setAttribute("STATES_ABBR_LIST", states);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
			dispatcher.forward(request, response);
			if (stmt != null)
				stmt.close();
			if (rs != null)
				rs.close();
			if (conn != null)
				conn.close();
			if (pgConnection != null)
				((Connection) pgConnection).close();
		}
	}

	/**
	 * This method has been replaced by createTable2 in this version of the program.
	 * 
	 * @param request information sent to servlet
	 * @param response information returned from servlet
	 * @throws Exception when an error occurs
	 */
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
		//String path = context.getRealPath("/");
		String path = "/tmp/";
		//File folder = (File) getServletContext().getAttribute(ServletContext.TEMPDIR);
		//File result = new File(folder, "filename.xml");
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

	/**
	 * Creates the Results table based on the user selection of state, start date,
	 * and end date. This method is called when the user clicks on "View State
	 * Data".
	 * 
	 * @param request information sent to servlet
	 * @param response information returned from servlet
	 * @throws Exception when an error occurs
	 */
	private void createTable2(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String mmddB = request.getParameter("start_date");
		String mmddA = request.getParameter("end_date");
		Database db = new Database();

		Connection conn = null;

		try {
			conn = dataSource.getConnection();
			Database.dropTable(conn, "results");
			String state = request.getParameter("state");
			db.selectByStateNoOutput(conn, state);
			db.deleteOldRecords(conn, "results", mmddB);
			db.deleteRecordsAfter(conn, "results", mmddA);
			Database.dropTable(conn, "positive");
			Database.dropTable(conn, "hospitalizations");
			Database.dropTable(conn, "death");
			Database.dropTable(conn, "coviddata3");
			listResults(request, response);
		}
		catch (Exception e) {
			System.err.println("CREATE ERROR");
			e.printStackTrace();
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * Deletes the Result record identified by the ID. This method is called when
	 * the user clicks on "Delete"
	 * 
	 * @param request information sent to servlet
	 * @param response information returned from servlet
	 * @throws Exception when an error occurs
	 */
	public void deleteResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int id = Integer.parseInt(request.getParameter("resultID"));
		resultDbUtil.deleteResult(id);
		listResults(request, response);
	}

	/**
	 * Updates the Result record identified by the ID. This method is called when
	 * the user clicks on "Save" on the Update form.
	 * 
	 * @param request information sent to servlet
	 * @param response information returned from servlet
	 * @throws Exception when an error occurs
	 */
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

	/**
	 * Loads the record to be updated into the Update Form. This method is called
	 * when the user clicks on "Edit"
	 * 
	 * @param request information sent to servlet
	 * @param response information returned from servlet
	 * @throws Exception when an error occurs
	 */
	private void loadResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("resultID");
		Result result = resultDbUtil.getResult(id);
		request.setAttribute("THE_RESULT", result);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/update-entry-form2.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * Adds a new Result object (record entry) to the Results table in the database.
	 * This method is called when the user clicks on Save on the "Add Entry" form.
	 * 
	 * @param request information sent to servlet
	 * @param response information returned from servlet
	 * @throws Exception when an error occurs
	 */
	private void addResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
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
			Result result = new Result(sqlDate, state, pos, hosp, death);
			resultDbUtil.addResult(result);
			listResults(request, response);
		}
		catch (NumberFormatException e) {
			System.out.println("One or more fields cannot be blank");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * Generates a list of results based on user selection of state, start date, and
	 * end date. This method is called (indirectly) after user clicks on View State Data, after
	 * the tables are created and also when user clicks on "Back to List".
	 * 
	 * @param request information sent to servlet
	 * @param response information returned from servlet
	 * @throws Exception when an error occurs
	 */
	private void listResults(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Result> results = resultDbUtil.getResults();
		request.setAttribute("RESULT_LIST", results);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-results.jsp");
		dispatcher.forward(request, response);

	}

}
