package com.databaseproject;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

/**
 * This class creates a Database object and implements the CRUD functionalities
 * that are necessary for a relational database. The methods that perform each
 * of the CRUD tasks are listed below.
 * <p>
 * <b>CREATE</b>
 * <ul>
 * <li><code><b>createDatabaseAndUser</b>(String[] db1ConnectionString, String[]
 * db2ConnectionString)</code></li>
 * <li><code><b>convertToTable</b>(Connection connection, String tableName, String allFields)
 * </code></li>
 * <li><code><b>createTable</b>(Connection connection, String tableName, String columns)</code></li>
 * </ul>
 * <p>
 * <b>READ</b>
 * <ul>
 * <li><code><b>selectByState</b>(Connection connection, String state)</code></li>
 * <li><code><b>selectByState</b>(Connection connection, String tableName, String state)</code></li>
 * <li><code><b>selectByDate</b>(Connection connection, String date_user)
 * </code></li>
 * <li><code><b>selectByDate</b>(Connection connection, String tableName, String date_user)
 * </code></li>
 * <li><code><b>selectByStateDate</b>(Connection connection, String state, String date_user)</code></li>
 * <li><code><b>selectByStateDate</b>(Connection connection, String tableName, String state,
 * String date_user)</code></li>
 * </ul>
 * <p>
 * <b>UPDATE</b>
 * <ul>
 * <li><code><b>alterUser</b>(String db, String username, String password)</code></li>
 * <li><code><b><a href=
 * "#addRecords(java.sql.Connection,java.lang.String,java.lang.String)">addRecords</a></b>(Connection connection, String filename, String tableName)</code></li>
 * </ul>
 * <p>
 * <b>DELETE</b>
 * <ul>
 * <li><code><b>dropTable</b>(Connection connection, String tableName)</code></li>
 * <li><code><b>deleteOldRecords</b>(Connection connection, String tableName, String
 * specifiedDate)</code></li>
 * <li><code><b>deleteDatabaseAndUser</b>(Connection conn, String dbName, String user)</code></li>
 * </ul>
 * 
 * @author Rachel Friedman
 * @version 2.0 This version introduces Java Servlets and JSP
 */
public class Database {

	public String host = "jdbc:postgresql://localhost:5432/";
	String dbName = "";
	String username = "";
	String password = "";
	public Connection connection = null;

	/**
	 * Creates a Database object with a specified database name, username and
	 * password
	 * 
	 * @param db       the database name
	 * @param username the username
	 * @param password the password
	 */
	public Database(String db, String username, String password) {
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(host + db, username, password);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		catch (SQLException e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("\nConnected to database " + db + " successfully");
	}

	/**
	 * Creates an empty database object with no set parameters
	 */
	public Database() {
	}

	/**
	 * Retrieves the database connection
	 * 
	 * @return the database connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Creates database with name specified. Creates user with user and password as
	 * specified. Grants user all privileges for newly created database.
	 * 
	 * @param db1ConnectionString initial connection to default database
	 * @param db2ConnectionString connection to new database. Includes database
	 *                            name, username and password.
	 */
	public void createDatabaseAndUser(String[] db1ConnectionString, String[] db2ConnectionString) {
		String username = db2ConnectionString[0];
		String password = db2ConnectionString[1];
		String dbName = db2ConnectionString[2];
		try {
			Connection Conn = DriverManager.getConnection(db1ConnectionString[0], db1ConnectionString[1],
					db1ConnectionString[2]);
			Statement Stmt = Conn.createStatement();
			try {
				Stmt.execute("DROP DATABASE IF EXISTS " + dbName + ";");
				Stmt.execute("CREATE DATABASE " + dbName + ";");
				Stmt.execute("CREATE USER " + username + " PASSWORD \'" + password + "\';");
				Stmt.execute("GRANT ALL PRIVILEGES ON DATABASE " + dbName + " TO " + username + ";");
				Conn.close();
				// console2.close();
				System.out.println("Database " + dbName + " created for user " + username);
			}
			catch (SQLException e) {
				System.out.println("User already exists. Changing password to new password instead");
				alterUser(db1ConnectionString[0], username, password);
			}
		}
		catch (SQLException e) {
			System.err.println("createDatabaseAndUser" + e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/**
	 * Alters password for specified user with given password
	 * 
	 * @param db       the initial database for creating this user
	 * @param username the user whose password is to be changed
	 * @param password the new password for the given user
	 */
	public void alterUser(String db, String username, String password) {
		try {
			Connection Conn = DriverManager.getConnection(db, username, password);
			Statement Stmt = Conn.createStatement();
			Stmt.execute("ALTER USER " + username + " PASSWORD \'" + password + "\';");
		}
		catch (SQLException e) {
			System.out.println("Unable to change user password.");
		}
	}

	/**
	 * Connects to database db with specified username and password
	 * 
	 * @param db       the database to connect to
	 * @param username the username to connect with
	 * @param password the password to connect with
	 * @return the database connection
	 */
	public Connection connectToDatabase(String db, String username, String password) {
		// Connection connection = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + db, username, password);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		catch (SQLException e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("\nConnected to database " + db + " successfully");
		return connection;
	}

	/**
	 * Drops the specified table.
	 * 
	 * @param connection the connection to the current database
	 * @param tableName  the table to be dropped
	 */
	public static void dropTable(Connection connection, String tableName) {
		try {
			Statement stmt = connection.createStatement();
			String sql = "DROP TABLE IF EXISTS " + tableName + ";";
			stmt.executeUpdate(sql);
			stmt.close();
			// System.out.println("Dropped table " + tableName);
		}
		catch (SQLException e) {
			System.err.println("Error dropping table " + e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Creates a table specified by tableName, using allFields from a csv file as
	 * columns
	 * 
	 * @param connection the connection to the current database
	 * @param tableName  the name of the table to be created
	 * @param allFields  the column names and data types
	 */
	public void convertToTable(Connection connection, String tableName, String allFields) {
		try {
			Statement stmt = connection.createStatement();
			String sql = "DROP TABLE IF EXISTS " + tableName + ";";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" + allFields + "); ";
			stmt.executeUpdate(sql);
			stmt.close();
			// System.out.println("Created table: " + tableName);
		}
		catch (SQLException e) {
			System.err.println("trouble converting table");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Creates a table in this database with tableName as table and columns as
	 * specified. <br>
	 * This method also adds an ID column as the primary key. <br>
	 * This method also alters the column names.
	 * 
	 * @param connection the connection to the current database
	 * @param tableName  the name of the table to be created
	 * @param columns    the column names and data types
	 */
	public void createTable(Connection connection, String tableName, String columns) {
		try {
			Statement stmt = connection.createStatement();
			String sql = "DROP TABLE IF EXISTS " + tableName + ";";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE IF NOT EXISTS " + tableName + " AS(SELECT " + columns + " FROM covidData); ";
			stmt.executeUpdate(sql);
			sql = "ALTER TABLE  " + tableName + " ADD COLUMN ID serial PRIMARY KEY;";
			stmt.executeUpdate(sql);
			sql = "ALTER TABLE  " + tableName + " RENAME state to ST;";
			stmt.executeUpdate(sql);
			System.out.println("Created table: " + tableName);
			stmt.close();
		}
		catch (Exception e) {
			System.err.println("CREATE TABLE " + e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Creates a table in this database with tableName as table and columns as
	 * specified. <br>
	 * This method also adds an ID column as the primary key. <br>
	 * This method also alters the column names.
	 * 
	 * @param connection    the connection to the current database
	 * @param importedTable the name of the table of the import with the latest data
	 * @param newTableName  the name of the table to be created
	 * @param columns       the column names and data types
	 */
	public void createTable(Connection connection, String importedTable, String newTableName, String columns) {
		try {
			Statement stmt = connection.createStatement();
			String sql = "DROP TABLE IF EXISTS " + newTableName + ";";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE IF NOT EXISTS " + newTableName + " AS(SELECT " + columns + " FROM " + importedTable +");";
			stmt.executeUpdate(sql);
			sql = "ALTER TABLE  " + newTableName + " ADD COLUMN ID serial PRIMARY KEY;";
			stmt.executeUpdate(sql);
			sql = "ALTER TABLE  " + newTableName + " RENAME state to ST;";
			stmt.executeUpdate(sql);
			System.out.println("Created table: " + newTableName);
			stmt.close();
		}
		catch (Exception e) {
			System.err.println("CREATE TABLE " + e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Creates a table in the database using the results of a query
	 * 
	 * @param conn the database connection
	 * @param sql  the SQL SELECT statement
	 */
	public void createTableFromQuery(Connection conn, String sql) {
		try {
			Statement stmt = conn.createStatement();
			sql = "CREATE TABLE results AS " + sql;
			stmt.execute(sql);
			sql = "ALTER TABLE  results  ADD COLUMN ID serial PRIMARY KEY;";
			stmt.executeUpdate(sql);
			stmt.close();
		}
		catch (SQLException e) {
			System.err.println("createTableFromQuery " + e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/**
	 * Creates a table in the database using the results of a query
	 * 
	 * @param conn the database connection
	 * @param sql  the SQL SELECT statement
	 */
	public void createTableFromQuery(Connection conn, String tableName, String sql) {
		try {
			dropTable(connection, tableName);
			Statement stmt = conn.createStatement();
			sql = "CREATE TABLE " + tableName + " AS " + sql;
			stmt.execute(sql);
			sql = "ALTER TABLE  " + tableName + "  ADD COLUMN ID serial PRIMARY KEY;";
			stmt.executeUpdate(sql);
			stmt.close();
		}
		catch (SQLException e) {
			System.err.println("createTableFromQuery " + e.getClass().getName() + ": " + e.getMessage());
		}
	}

	
	/**
	 * Adds record to specified table by copying data from specified file.
	 * 
	 * @param connection the database connection
	 * @param filename   the csv file from which to copy the data
	 * @param tableName  the name of the table to add the records to
	 */
	public void addRecords(Connection connection, String filename, String tableName) {
		try {
			String sql = "copy " + tableName + " FROM stdin DELIMITER ',' CSV header";
			BaseConnection con = (BaseConnection) connection;
			CopyManager mgr = new CopyManager(con);
			try {
				Reader in = new BufferedReader(new FileReader(new File(filename)));
				long rowsaffected = mgr.copyIn(sql, in);
				System.out.println("Records imported for " + tableName + ": " + rowsaffected);
			}
			catch (FileNotFoundException e) {
				System.err.println(e.getClass().getName() + ": " + "File " + filename + " not found");
			}
		}
		catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/**
	 * Adds record to specified table by copying data from specified file using a
	 * pgConnection. this type of connection is required for running the web based
	 * version from apache tomcat.
	 * 
	 * @param connection the database PGConnection
	 * @param filename   the csv file from which to copy the data
	 * @param tableName  the name of the table to add the records to
	 */
	public void addRecords(PGConnection connection, String filename, String tableName) {
		try {
			String sql = "copy " + tableName + " FROM stdin DELIMITER ',' CSV header";
			BaseConnection con = (BaseConnection) connection;
			CopyManager mgr = new CopyManager(con);
			try {
				Reader in = new BufferedReader(new FileReader(new File(filename)));
				long rowsaffected = mgr.copyIn(sql, in);
				System.out.println("Records imported for " + tableName + ": " + rowsaffected);
			}
			catch (FileNotFoundException e) {
				System.err.println(e.getClass().getName() + ": " + "File " + filename + " not found");
			}
		}
		catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/**
	 * Deletes records from table tableName where date is before given date
	 * 
	 * @param connection    the connection to the database
	 * @param tableName     the table from which to delete the rows
	 * @param specifiedDate specifies the date from which to delete all records.
	 *                      Records on this date are not deleted.
	 */
	public void deleteOldRecords(Connection connection, String tableName, String specifiedDate) {
		try {
			String date = "\'2020-" + specifiedDate + "\'";
			Statement stmt = connection.createStatement();
			// String sql = "DELETE FROM " + tableName + " WHERE date < \'2020-03-15\'; ";
			String sql = "DELETE FROM " + tableName + " WHERE date < " + date + "; ";
			stmt.executeUpdate(sql);
			stmt.close();
			System.out.println("Removed all data prior to 2020-" + specifiedDate + "\n");
		}
		catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Deletes records from table tableName where date is after given date
	 * 
	 * @param connection    the connection to the database
	 * @param tableName     the table from which to delete the rows
	 * @param specifiedDate specifies the date from which to delete all records.
	 *                      Records on this date are not deleted.
	 */
	public void deleteRecordsAfter(Connection connection, String tableName, String specifiedDate) {
		try {
			String date = "\'2020-" + specifiedDate + "\'";
			Statement stmt = connection.createStatement();
			// String sql = "DELETE FROM " + tableName + " WHERE date < \'2020-03-15\'; ";
			String sql = "DELETE FROM " + tableName + " WHERE date > " + date + "; ";
			stmt.executeUpdate(sql);
			stmt.close();
			System.out.println("Removed all data after 2020-" + specifiedDate + "\n");
		}
		catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Deletes this session's database and user. This method is typically used to
	 * perform cleanup when exiting a program.
	 * 
	 * @param conn   the connection to the database
	 * @param dbName the database to be deleted
	 * @param user   the user to be deleted
	 */
	public void deleteDatabaseAndUser(Connection conn, String dbName, String user) {
		try {
			conn.close();
			Connection Conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "postgres");
			Statement Stmt = Conn.createStatement();
			Stmt.execute("DROP DATABASE IF EXISTS " + dbName + ";");
			Stmt.execute("DROP USER " + user + ";");
			System.out.println("\nDeleting database and user for current session... Done.");
		}
		catch (SQLException e) {
			// System.err.println("dropDatabase " + e.getClass().getName() + ": " +
			// e.getMessage());
			System.out.println("Unable to delete current session user and database. Delete manually.");
		}
	}

	/**
	 * Performs a "select by state" query and returns positive cases,
	 * hospitalizations and deaths by joining three tables for a specific state,
	 * sorted by latest date first
	 * 
	 * @param connection the connection to the database
	 * @param state      the specific state to fetch data for
	 * @return the results of the query formatted in an html table
	 */
	public void selectByStateNoOutput(Connection connection, String state) {
		try {
			Statement stmt = connection.createStatement();
			String sql = "SELECT positive.date, positive.st, positive, hospitalizedcumulative, death FROM positive JOIN hospitalizations On positive.id = hospitalizations.id JOIN death ON positive.id = death.id WHERE positive.st ="
					+ " \'" + state.toUpperCase() + "\' ORDER BY date asc;";
			System.out.println("Retrieving records for state = " + state.toUpperCase());
			createTableFromQuery(connection, sql);		
			stmt.close();
		}
		catch (Exception e) {
			System.err.println("selectByStateNoOutput " + e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	/**
	 * Performs a "select by state" query and returns positive cases,
	 * hospitalizations and deaths by joining three tables for a specific state,
	 * sorted by latest date first
	 * 
	 * @param connection the connection to the database
	 * @param state      the specific state to fetch data for
	 * @return the results of the query formatted in an html table
	 */
	public String selectByState(Connection connection, String state) {
		String htmlResults = "";
		String pattern = "MMMM d";
		DateFormat df = new SimpleDateFormat(pattern);
		DecimalFormat decF = new DecimalFormat("#,###");

		try {
			Statement stmt = connection.createStatement();
			String sql = "SELECT positive.date, positive.st, positive, hospitalizedcumulative, death FROM positive JOIN hospitalizations On positive.id = hospitalizations.id JOIN death ON positive.id = death.id WHERE positive.st ="
					+ " \'" + state.toUpperCase() + "\' ORDER BY date desc;";
			System.out.println("Retrieving records for state = " + state.toUpperCase());
			ResultSet rs = stmt.executeQuery(sql);
			createTableFromQuery(connection, sql);

			while (rs.next()) {
				String st = rs.getString("st");
				Date date = rs.getDate("date");
				String dateF = df.format(date);
				int pos = rs.getInt("positive");
				String poss = decF.format(pos);
				int hosp = rs.getInt("hospitalizedCumulative");
				String hosps = decF.format(hosp);
				if (hosp == 0)
					hosps = "NA";
				int death = rs.getInt("death");
				String deaths = decF.format(death);
				htmlResults = htmlResults + "\n\t\t\t\t<tr> <td> " + st + "</td> <td>" + date + "</td> <td> " + poss
						+ "</td> <td> " + hosps + "</td> <td> " + deaths + "</td> </tr>";
				// System.out.println(st + " | " + hosps + " | " + poss + " | " + deaths);
			}
			rs.close();
			stmt.close();
		}
		catch (Exception e) {
			System.err.println("selectByState " + e.getClass().getName() + ": " + e.getMessage());
		}
		return htmlResults;
	}

	/**
	 * Performs a "select by state" query on table specified
	 * 
	 * @param connection the connection to the database
	 * @param tableName  the table to be queried
	 * @param state      the specific state to fetch data for
	 * @return the results of the query formatted in an html table
	 */
	public String selectByState(Connection connection, String tableName, String state) {
		String htmlResults = "";
		String pattern = "MMMM d";
		DateFormat df = new SimpleDateFormat(pattern);
		DecimalFormat decF = new DecimalFormat("#,###");

		try {
			Statement stmt = connection.createStatement();
			String sql = "SELECT state, date, positive, hospitalizedCumulative, death FROM " + tableName
					+ " WHERE state = \'" + state.toUpperCase() + "\' ORDER BY date desc;";
			System.out.println("Retrieving records from " + tableName + " for state=" + state.toUpperCase());
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String st = rs.getString("state");
				Date date = rs.getDate("date");
				String dateF = df.format(date);
				int pos = rs.getInt("positive");
				String poss = decF.format(pos);
				int hosp = rs.getInt("hospitalizedCumulative");
				String hosps = decF.format(hosp);
				if (hosp == 0)
					hosps = "NA";
				int death = rs.getInt("death");
				String deaths = decF.format(death);
				htmlResults = htmlResults + "\n\t\t\t\t<tr> <td> " + st + "</td> <td>" + date + "</td> <td> " + poss
						+ "</td> <td> " + hosps + "</td> <td> " + deaths + "</td> </tr>";
				System.out.println(st + " | " + hosps + " | " + poss + " | " + deaths);
			}
			rs.close();
			stmt.close();
		}
		catch (Exception e) {
			System.err.println("selectByState " + e.getClass().getName() + ": " + e.getMessage());
		}
		return htmlResults;
	}

	/**
	 * Performs a "select by date" query and returns positive cases,
	 * hospitalizations and deaths by joining three tables for a specific date,
	 * sorted by decreasing positive cases
	 * 
	 * @param connection the connection to the database
	 * @param date_user  the specific date to fetch data for
	 * @return the results of the query formatted in an html table
	 */
	public String selectByDate(Connection connection, String date_user) {
		String htmlResults = "";
		String pattern = "MMMM d";
		DateFormat df = new SimpleDateFormat(pattern);
		DecimalFormat decF = new DecimalFormat("#,###");

		try {
			Statement stmt = connection.createStatement();
			String sql = "SELECT positive.date, positive.st, positive, hospitalizedcumulative, death FROM positive JOIN hospitalizations On positive.id = hospitalizations.id JOIN death ON positive.id = death.id WHERE positive.date ="
					+ " \'2020-" + date_user + "\' ORDER BY positive desc;";
			System.out.println("Retrieving records from for date = 2020-" + date_user);
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String st = rs.getString("st");
				Date date = rs.getDate("date");
				String dateF = df.format(date);
				int pos = rs.getInt("positive");
				String poss = decF.format(pos);
				int hosp = rs.getInt("hospitalizedCumulative");
				String hosps = decF.format(hosp);
				if (hosp == 0)
					hosps = "NA";
				int death = rs.getInt("death");
				String deaths = decF.format(death);
				htmlResults = htmlResults + "\n<tr> <td> " + dateF + "</td> <td>" + st + "</td> <td> " + poss
						+ "</td> <td> " + hosps + "</td> <td> " + deaths + "</td> </tr>";
				// System.out.println(st + " | " + hosps + " | " + poss + " | " + deaths);
			}
			rs.close();
			stmt.close();
		}
		catch (Exception e) {
			System.out.println("Oops. Invalid date format. Please try again.");
			// runQueries(connection, tableName);
			// System.exit(0);
			// System.err.println("selectByDate " + e.getClass().getName() + ": " +
			// e.getMessage());
		}

		return htmlResults;
	}

	/**
	 * Performs a "select by date" query on table specified
	 * 
	 * @param connection the connection to the database
	 * @param tableName  the table to be queried
	 * @param date_user  the specific date to fetch data for
	 * @return the results of the query formatted in an html table
	 */
	public String selectByDate(Connection connection, String tableName, String date_user) {
		String htmlResults = "";
		String pattern = "MMMM d";
		DateFormat df = new SimpleDateFormat(pattern);
		DecimalFormat decF = new DecimalFormat("#,###");

		try {
			Statement stmt = connection.createStatement();
			String sql = "SELECT date, state, positive, hospitalizedCumulative, death FROM " + tableName
					+ " WHERE date = \'2020-" + date_user + "\' ORDER BY positive desc;";
			System.out.println("Retrieving records from " + tableName + " for date = 2020-" + date_user);
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String st = rs.getString("state");
				Date date = rs.getDate("date");
				String dateF = df.format(date);
				int pos = rs.getInt("positive");
				String poss = decF.format(pos);
				int hosp = rs.getInt("hospitalizedCumulative");
				String hosps = decF.format(hosp);
				if (hosp == 0)
					hosps = "NA";
				int death = rs.getInt("death");
				String deaths = decF.format(death);
				htmlResults = htmlResults + "\n<tr> <td> " + dateF + "</td> <td>" + st + "</td> <td> " + poss
						+ "</td> <td> " + hosps + "</td> <td> " + deaths + "</td> </tr>";
				System.out.println(st + " | " + hosps + " | " + poss + " | " + deaths);
			}
			rs.close();
			stmt.close();
		}
		catch (Exception e) {
			System.out.println("Oops. Invalid date format. Please try again.");
			// runQueries(connection, tableName);
			// System.exit(0);
			// System.err.println("selectByDate " + e.getClass().getName() + ": " +
			// e.getMessage());
		}

		return htmlResults;
	}

	/**
	 * Performs a "select by state and by date" query on table specified
	 * 
	 * @param connection the connection to the database
	 * @param tableName  the table to be queried
	 * @param state      the specific state to fetch data for
	 * @param date_user  the specific date to fetch data for
	 * @return the results of the query formatted in an html table
	 */
	public String selectByStateDate(Connection connection, String tableName, String state, String date_user) {
		String htmlResults = "";
		String pattern = "MMMM d";
		DateFormat df = new SimpleDateFormat(pattern);
		DecimalFormat decF = new DecimalFormat("#,###");

		try {
			Statement stmt = connection.createStatement();
			String sql = "SELECT date, state, positive, hospitalizedCumulative, death FROM " + tableName
					+ " WHERE date = \'2020-" + date_user + "\' AND state=  \'" + state.toUpperCase()
					+ "\' ORDER BY date asc;";
			System.out.println("Retrieving records from " + tableName + " for state = " + state.toUpperCase()
					+ " and date = 2020-" + date_user);
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String st = rs.getString("state");
				Date date = rs.getDate("date");
				String dateF = df.format(date);
				int pos = rs.getInt("positive");
				String poss = decF.format(pos);
				int hosp = rs.getInt("hospitalizedCumulative");
				String hosps = decF.format(hosp);
				if (hosp == 0)
					hosps = "NA";
				int death = rs.getInt("death");
				String deaths = decF.format(death);
				htmlResults = htmlResults + "\n<tr> <td> " + dateF + "</td> <td>" + st + "</td> <td> " + poss
						+ "</td> <td> " + hosps + "</td> <td> " + deaths + "</td> </tr>";
				// System.out.println(st + " | " + hosps + " | " + poss + " | " + deaths);
			}
			rs.close();
			stmt.close();
		}
		catch (Exception e) {
			System.out.println("Oops. Invalid date format. Please try again.");
			// runQueries(connection, tableName);
			// System.err.println("selectByStateDate " + e.getClass().getName() + ": " +
			// e.getMessage());
		}
		return htmlResults;
	}

	/**
	 * Performs a "select by state and date" query and returns positive cases,
	 * hospitalizations and deaths by joining three tables for a specific state on a
	 * specific date
	 * 
	 * @param connection the connection to the database
	 * @param state      the specific state to fetch data for
	 * @param date_user  the specific date to fetch data for
	 * @return the results of the query formatted in an html table
	 */
	public String selectByStateDate(Connection connection, String state, String date_user) {
		String htmlResults = "";
		String pattern = "MMMM d";
		DateFormat df = new SimpleDateFormat(pattern);
		DecimalFormat decF = new DecimalFormat("#,###");

		try {
			Statement stmt = connection.createStatement();
			String sql = "SELECT positive.date, positive.st, positive, hospitalizedcumulative, death FROM positive JOIN hospitalizations On positive.id = hospitalizations.id JOIN death ON positive.id = death.id WHERE positive.date = \'2020-"
					+ date_user + "\' AND positive.st=  \'" + state.toUpperCase() + "\';";
			System.out
					.println("Retrieving records for state = " + state.toUpperCase() + " and date = 2020-" + date_user);
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String st = rs.getString("st");
				Date date = rs.getDate("date");
				String dateF = df.format(date);
				int pos = rs.getInt("positive");
				String poss = decF.format(pos);
				int hosp = rs.getInt("hospitalizedCumulative");
				String hosps = decF.format(hosp);
				if (hosp == 0)
					hosps = "NA";
				int death = rs.getInt("death");
				String deaths = decF.format(death);
				htmlResults = htmlResults + "\n<tr> <td> " + dateF + "</td> <td>" + st + "</td> <td> " + poss
						+ "</td> <td> " + hosps + "</td> <td> " + deaths + "</td> </tr>";
				// System.out.println(st + " | " + hosps + " | " + poss + " | " + deaths);
			}
			rs.close();
			stmt.close();
		}
		catch (Exception e) {
			System.out.println("Oops. Invalid date format. Please try again.");
			// runQueries(connection, tableName);
			// System.err.println("selectByStateDate " + e.getClass().getName() + ": " +
			// e.getMessage());
		}
		return htmlResults;
	}

	/**
	 * Creates a HashMap of the full name of the abbreviation of each state.<br>
	 * This is used when checking to see if valid state was entered. This is also
	 * used when spelling out full name of state in place of abbreviation.
	 * 
	 * @return a Hashmap of the abbreviations and full names of each of the 50
	 *         states and 6 territories.
	 */
	public HashMap<String, String> createListOfStates() {
		HashMap<String, String> statesList = new HashMap<String, String>();
		try {
			Statement stmt = connection.createStatement();
			String sql = "SELECT ST, state FROM states ORDER BY ST asc;";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String st = rs.getString("st");
				String state = rs.getString("state");
				state = state.replace(" ", "_");
				statesList.put(st, state);
			}
			rs.close();
			stmt.close();
		}
		catch (Exception e) {
			System.out.println("Oops. Invalid date format. Please try again.");
			System.err.println("createListOfStates " + e.getClass().getName() + ": " + e.getMessage());
		}
		return statesList;
	}
}