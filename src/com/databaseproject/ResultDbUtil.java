package com.databaseproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.sql.DataSource;

/**
 * This is a helper class for Result, which is the Model in the MVC.  This class is used by ResultControllerServlet to interact with the Result class.
 * @author Rachel Friedman
 *
 */
public class ResultDbUtil {

	private DataSource dataSource;
	Database db = new Database();
	
	/**
	 * constructs a ReslultDbUtil object which acts as the go-between between Result and the ResultControllerServlet
	 * @param theDataSource is used to get a connection to the database
	 */
	public ResultDbUtil(DataSource theDataSource) {
		dataSource = theDataSource;
//		try {
//
//		Class.forName("jdbc:postgresql://ec2-54-158-1-189.compute-1.amazonaws.com:5432/ddgha774rb1b8u?user=ukydkheqlvsoyq&password=d285def601ef8b0277c159f27290138b322b733e8d2484e80c709be4d9ea8fd6");
////			Class.forName("jdbc:postgresql://aaww400sexm5z3ds:5432/dbName?user=rfriedman113Name&password=us04web.");
//		}
//		catch(Exception e) {
//			System.out.println("Class.forName(className) not working!");
//		}
	}

	/**
	 * Generates a list of Result objects, where each Result object represents one record in the database returned by the database query
	 * @return a a list of Result objects, where each Result object represents one record in the database returned by the database query
	 * @throws Exception when an error occurs
	 */
	public List<Result> getResults() throws Exception {

		List<Result> results = new ArrayList<Result>();

		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;

		try {
			// get connection
			myConn = dataSource.getConnection();

			// create sql statement
			String sql = "SELECT * FROM results;";
			myStmt = myConn.createStatement();

			// run query
			myRs = myStmt.executeQuery(sql);

			// process result set
			while (myRs.next()) {
				// retrieve data from resultset row
				int id = myRs.getInt("id");
				Date date = myRs.getDate("date");
				String state = myRs.getString("st");
				int pos = myRs.getInt("positive");
				int hosp = myRs.getInt("hospitalizedcumulative");
				int death = myRs.getInt("death");

				// create new result object
				Result tempResult = new Result(id, date, state, pos, hosp, death);

				// add it to the results list
				results.add(tempResult);
			}
			// return results;

		}
		catch (Exception e) {
			System.err.println("getResults " + e.getClass().getName() + e.getMessage());
		}
		finally {
			// close JDBC objects
			close(myConn, myStmt, myRs);

		}
		return results;
	}
		
	/**
	 * Updates the table by adding a new Result object (which is one record) into the table
	 * @param result represents the new record to be added to the database
	 * @throws Exception when an error occurs
	 */

	public void addResult(Result result) throws Exception {
		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			// create sql for insert
			myConn = dataSource.getConnection();
			String sql = "INSERT INTO results " + "(date, st, positive, hospitalizedcumulative, death)"
					+ "values(?, ?, ?, ?, ?)";
			myStmt = myConn.prepareStatement(sql);

			// set the param values for the sql values
			myStmt.setDate(1, result.getDate());
			myStmt.setString(2, result.getState());
			myStmt.setInt(3, result.getPositive());
			myStmt.setInt(4, result.getHospitalizations());
			myStmt.setInt(5, result.getDeath());

			// execute sql insert
			myStmt.execute();

		}
		finally {// clean up JDBC
			close(myConn, null, null);
		}
	}

	/**
	 * Retrieves a record based on ID 
	 * @param id the ID used to identify the record being retrieved
	 * @return Result object, which is a single record in the database
	 * @throws Exception when an error occurs
	 */
	public Result getResult(String id) throws Exception {
		Result result = null;

		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		int resultId = Integer.parseInt(id);

		try {
			// get connection to database
			myConn = dataSource.getConnection();

			// sql to get selected Result
			String sql = "SELECT * FROM RESULTS WHERE id = ?";

			// create prepared statement
			myStmt = myConn.prepareStatement(sql);

			// set parameters to know which result id to get
			myStmt.setInt(1, resultId);

			// execute statement
			myRs = myStmt.executeQuery();

			// process data from result set
			if (myRs.next()) {
				Date date = myRs.getDate("date");
				String state = myRs.getString("st");
				int pos = myRs.getInt("positive");
				int hosp = myRs.getInt("hospitalizedcumulative");
				int death = myRs.getInt("death");

				result = new Result(resultId, date, state, pos, hosp, death);
			}
			else {
				throw new Exception("Could not find result id: " + id);
			}

			return result;
		}
		finally {
			close(myConn, myStmt, myRs);
		}
	}

	/**
	 * Updates a result object (record) 
	 * @param theResult the newly updated result (record)
	 * @throws Exception when an error occurs
	 */
	public void updateResult(Result theResult) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			// convert Result to int
			// get connection to database
			myConn = dataSource.getConnection();

			// create sql update statement
			String sql = "UPDATE RESULTS SET date=?, st=?, positive=?, hospitalizedcumulative=?, death=? WHERE id = ?";

			// create prepared statement
			myStmt = myConn.prepareStatement(sql);

			// set parameters for that statement
			myStmt.setDate(1, theResult.getDate());
			myStmt.setString(2, theResult.getState());
			myStmt.setInt(3, theResult.getPositive());
			myStmt.setInt(4, theResult.getHospitalizations());
			myStmt.setInt(5, theResult.getDeath());
			myStmt.setInt(6, theResult.getId());
			// execute statement to perform update on database

			myStmt.execute();
		}

		finally {
			// close connections
			close(myConn, myStmt, null);
		}
	}

	/**
	 * Deletes the result object (record) based on ID
	 * @param id the ID of the result (record) to be deleted
	 * @throws Exception when an error occurs
	 */
	public void deleteResult(int id) throws Exception {
		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {

			// get connection to database
			myConn = dataSource.getConnection();

			// create sql update statement
			String sql = "DELETE FROM RESULTS WHERE id = ?";

			// create prepared statement
			myStmt = myConn.prepareStatement(sql);

			// set parameters for that statement
			myStmt.setInt(1, id);

			// execute statement to perform update on database
			myStmt.execute();
		}

		finally {
			// close connections
			close(myConn, myStmt, null);
		}

	}

	private void close(Connection myConn, Statement myStmt, ResultSet myRs) {
		try {
			if (myRs != null) {
				myRs.close();
			}
			if (myStmt != null) {
				myStmt.close();
			}
			if (myConn != null) {
				myConn.close();
			}
		}
		catch (Exception e) {
			System.err
					.println("close " + e.getStackTrace()[0].getMethodName() + e.getClass().getName() + e.getMessage());
		}

	}

	/*
	 * Closes connection to database
	 * @throws SQLException when a database error occurs
	 */
	public void exit() throws SQLException{
		Connection conn = null;	
		try {
			conn = dataSource.getConnection();
			Database.dropTable(conn, "results");
			Database.dropTable(conn, "positive");
			Database.dropTable(conn, "hospitalizations");
			Database.dropTable(conn, "death");
			Database.dropTable(conn, "coviddata3");
			System.out.println("DELETING ALL TABLES. EXITING NOW");
		}
		catch (Exception e) {
			System.out.println("Exit error");
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}
		
	/**
	 * Creates a HashMap of the fifty states with its abbreviations. This is used to populate the State field for user selection.
	 * @return a HashMap of the fifty states with its abbreviations
	 */
	public LinkedHashMap<String, String> getListOfStates() {
		LinkedHashMap<String, String> states = new LinkedHashMap<String, String>();
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet rs = null;

		try {
			System.out.println("this is BEFORE myConn=dataSource.getConnection");
			myConn = dataSource.getConnection();
			System.out.println("this is AFTER myConn=dataSource.getConnection");
			Statement stmt = myConn.createStatement();
			String sql = "SELECT ST, state FROM states ORDER BY ST asc;";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String st = rs.getString("st");
				String state = rs.getString("state");
				state = state.replace(" ", "_");
				states.put(st, state);
			}
			rs.close();
			stmt.close();
			
//			for (String url : states.values())
//				System.out.println("value: " + url);
		}
		catch (Exception e) {
			System.err.println("Error populating states in form" + e.getClass().getName() + ": " + e.getMessage());
		}
		close(myConn, myStmt, rs);
		return states;

	}
}
