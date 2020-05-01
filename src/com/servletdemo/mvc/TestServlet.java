package com.servletdemo.mvc;

import com.databaseproject.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.DataSourceProxy;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.postgresql.PGConnection;
import org.postgresql.core.BaseConnection;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private ResultDbUtil resultDbUtil;
	@Resource(name = "jdbc/covid")
	private DataSource dataSource;
	String address = "https://covidtracking.com/api/v1/states/daily.csv";

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

		String tableWithLatestData = "coviddata3";
		Database db = new Database();
		WebScraper ws = new WebScraper();
		byte[] content = ws.retrieveDataFromWebsite(address);
		String path = "C:\\Users\\Rachel\\eclipse-workspace\\Covid19\\data\\";
		String filename = path + "data.csv";
		String columns = ws.saveToFile(filename, content);

		// step 1 set up printwriter
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");

		// step 2 get a connection to the database
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String state = request.getParameter("state");
		out.println("State = " + state);
		out.println("Succcess!");
		out.println(columns);

		try {
			conn = dataSource.getConnection();
			db.convertToTable(conn, tableWithLatestData, columns);
			PGConnection pgConnection = conn.unwrap(PGConnection.class);
			db.addRecords(pgConnection, filename, tableWithLatestData);
			db.createTable(conn, tableWithLatestData, "positive", "date, state, positive");
			db.createTable(conn, tableWithLatestData, "hospitalizations", "date, state, hospitalizedcumulative");
			db.createTable(conn, tableWithLatestData, "death", "date, state, death");
			db.dropTable(conn, "results");
			db.selectByStateNoOutput(conn, state);
			List<Result> results = resultDbUtil.getResults();
			request.setAttribute("RESULT_LIST", results);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/list-results.jsp");
			dispatcher.forward(request, response);

			// step 3 create sql statement
			String sql = "select * from results limit 5;";
			stmt = conn.createStatement();

			// step 4 execute sql query
			rs = stmt.executeQuery(sql);
			// step 5 process result set

			while (rs.next()) {
				int cases_new = rs.getInt("positive");
				out.println(cases_new);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createTable(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
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
