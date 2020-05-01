package com.databaseproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.sql.DataSource;

public class ResultDbUtil {

	private DataSource dataSource;

	public ResultDbUtil(DataSource theDataSource) {
		dataSource = theDataSource;
//		try {
//			Class.forName("jdbc:postgresql://aaww400sexm5z3ds:5432/dbName?user=rfriedman113Name&password=us04web.");
//		}
//		catch(Exception e) {
//			System.out.println("Class.forName(className) not working!");
//		}
	}

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

	public void addResult(Result result) throws Exception {
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;

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
			close(myConn, myStmt, null);
		}
	}

	public Result getResult(String id) throws Exception {
		Result result = null;

		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		int resultId = Integer.parseInt(id);

		try {
			// convert Result to int
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
			// close connections
			close(myConn, myStmt, myRs);
		}
	}

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

	public LinkedHashMap<String, String> getListOfStates() {
		LinkedHashMap<String, String> states = new LinkedHashMap<String, String>();
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet rs = null;

		try {
			myConn = dataSource.getConnection();
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
			System.err.println("hashmapping states error " + e.getClass().getName() + ": " + e.getMessage());
		}
		close(myConn, myStmt, rs);
		return states;

	}
}
