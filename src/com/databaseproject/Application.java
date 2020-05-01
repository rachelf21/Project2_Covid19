package com.databaseproject;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.*;
import java.sql.Connection;

/**
 * This class launches the program. <br>
 * This program downloads covid19 tracking information for each of the fifty
 * states and six territories in the United States, from
 * <a href="https://covidtracking.com/api" target="_blank">
 * covidtracking.com</a>. It then creates and accesses a PostgreSQL database,
 * based on the following user specifications: database name, username and
 * password. The user is granted full access to the database. The following
 * tables are then created: states, positive, hospitalizations, and deaths. The
 * user is then presented with options to query the database and to display data
 * by state, date, or both state and date. The results are then output as an
 * HTML document, formatted with DataTable in Bootstrap 4. Upon completion, the
 * program deletes the session's database and user. The result of the queries
 * remain saved as html files in an output folder.
 * 
 * @author Rachel Friedman
 * @version 2.0 This version introduces Java Servlets and JSP
 */
public class Application {
	static int exit = 1;
	String path = "data\\";
	String filename = path + "data.csv";
	String tableName = "covidData";
	static String address = "https://covidtracking.com/api/v1/states/daily.csv";
	String[] initialConnection = { "jdbc:postgresql://localhost:5432/", "postgres", "postgres" };
	static String[] credentials = new String[3];
	static Scanner console = new Scanner(System.in);
	String datePattern = "[01][0-9]-[0-3][0-9]";

	/**
	 * Launches the program
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		HTMLWriter htmlWriter = new HTMLWriter();
		Application app = new Application();
		Database db = new Database();
		String columns = app.getData(address);
		Connection connection = app.setUpDatabase(db, columns);
		app.queryDatabase(connection, db, htmlWriter);
		
		if (exit == 0) {
			db.deleteDatabaseAndUser(connection, credentials[2], credentials[0]);
			System.gc();
			System.out.println("Exiting Program... Goodbye.");
			console.close();
			System.exit(0);
		}
	}

	/**
	 * Retrieves data from website
	 * 
	 * @param address URL address to get data from
	 * @return column names with data types
	 */
	public String getData(String address) {
		WebScraper ws = new WebScraper();
		byte[] content = ws.retrieveDataFromWebsite(address);
		String columnsWithDataTypes = ws.saveToFile(filename, content);
		return columnsWithDataTypes;
	}

	/**
	 * Performs all the required database setup: Creates the database, user, states
	 * table, positive table, hospitalizations table, death table
	 * 
	 * @param db      the database which is being set up
	 * @param columns the columns to use for the data that was downloaded
	 * @return Connection the connection to this database
	 */
	public Connection setUpDatabase(Database db, String columns) {
		System.out.println(
				"\nThis COVID-19 database allows user to track \nthe positive cases, hospitalizations and deaths \nin each of the 50 states and 6 US territories.\n");
		credentials = promptForCredentials();
		db.createDatabaseAndUser(initialConnection, credentials);
		Connection connection = db.connectToDatabase(credentials[2], credentials[0], credentials[1]);
		db.convertToTable(connection, tableName, columns);
		db.addRecords(connection, path + "data.csv", tableName);
		db.convertToTable(connection, "states", "id integer primary key, ST text, state text");
		db.addRecords(connection, path + "states.csv", "states");
		System.out.print("\nEnter starting date (mm-dd) for data to be stored in database: ");
		String date = console.next();
		while (!Pattern.matches(datePattern, date)) {
			System.out.print("Enter a valid date in this format mm-dd:  ");
			date = console.next();
		}
		db.deleteOldRecords(connection, tableName, date);
		db.createTable(connection, "positive", "date, state, positive");
		db.createTable(connection, "hospitalizations", "date, state, hospitalizedcumulative");
		db.createTable(connection, "death", "date, state, death");
		return connection;
	}

	/**
	 * Launches the menu with a list of queries to perform on the database
	 * 
	 * @param connection the database connection
	 * @param db         the database
	 * @param htmlWriter writes the output to an HTML file
	 */
	public void queryDatabase(Connection connection, Database db, HTMLWriter htmlWriter) {
		System.out.println("\n--QUERYING THE DATABASE--");
		printMenu(connection, db, htmlWriter);
		//TODO incorporate query methods from printMenu here
	}

	/**
	 * Prompts the user to select a username, password and name for database
	 * 
	 * @return the username, password, and name for database
	 */
	private String[] promptForCredentials() {
		String[] credentials = new String[3];
		System.out.println("\n--DATABASE SETUP--\n");
		System.out.print("Select a username: ");
		credentials[0] = console.next();
		System.out.print("Select a password: ");
		credentials[1] = console.next();
		System.out.print("Select a name for your database: ");
		credentials[2] = console.next();
		return credentials;
	}

	/**
	 * Prompts the user to select from a menu of different queries that can be
	 * performed on the database. <br>
	 * Calls upon the relevant method after user selects one of the options
	 * presented.
	 * 
	 * @param connection the connection to the database
	 * @param db         the database that contains the tables being queried
	 * @param html       the htmlWriter used to generate the html file
	 */
	public void printMenu(Connection connection, Database db, HTMLWriter html) {
		Scanner console = new Scanner(System.in);
		String state = "";
		String date = "";
		String htmlResults = "";
		int selection = 4;
		HashMap<String, String> hm = db.createListOfStates();

		try {
			do {
				System.out.println("\nHow would you like to query the data?");
				System.out.println("1| View by state");
				System.out.println("2| View by date");
				System.out.println("3| View by state and date");
				System.out.print("Enter 1, 2, 3 or 0 to exit: ");
				selection = console.nextInt();

				switch (selection) {
				case 0:
					exit = 0;
					break;
				case 1:
					System.out.print("\nEnter state: ");
					state = console.next().toUpperCase();
					while (!hm.containsKey(state)) {
						System.out.print("Invalid state");
						System.out.print("\nEnter state: ");
						state = console.next().toUpperCase();
					}
					htmlResults = db.selectByState(connection, state);
					saveAndViewResults(html, htmlResults, state, 1);
					break;
				case 2:
					System.out.print("\nEnter a date in this format mm-dd:  ");
					date = console.next();
					while (!Pattern.matches(datePattern, date)) {
						System.out.print("Enter a valid date in this format mm-dd:  ");
						date = console.next();
					}
					htmlResults = db.selectByDate(connection, date);
					saveAndViewResults(html, htmlResults, date, 2);
					break;
				case 3:
					System.out.print("\nEnter state: ");
					state = console.next().toUpperCase();
					while (!hm.containsKey(state)) {
						System.out.print("Invalid state");
						System.out.print("\nEnter state: ");
						state = console.next().toUpperCase();
					}
					System.out.print("Enter a date in this format mm-dd: ");
					date = console.next();
					while (!Pattern.matches(datePattern, date)) {
						System.out.print("Enter a valid date in this format mm-dd:  ");
						date = console.next();
					}
					htmlResults = db.selectByStateDate(connection, state, date);
					saveAndViewResults(html, htmlResults, hm.get(state) + "_" + date, 3);
					break;
				default:
					System.out.println("\nInvalid selection. Enter 1, 2, 3 or 0 to exit.");
				}
			} while (selection != 0 || selection > 3);
		}
		catch (InputMismatchException e) {
			System.out.println("\nInvalid entry. Enter 1, 2, 3 or 0 to exit.");
			printMenu(connection, db, html);
		}
	}

	/**
	 * Saves the query results in an HTML file. Prompts the user with an option to
	 * view the file.
	 * 
	 * @param html      the object that is used to write the HTML file
	 * @param results   the results of the query that are being saved to the html
	 *                  file
	 * @param selection the condition for the query
	 * @param option    the corresponding option that the user chose
	 */
	public void saveAndViewResults(HTMLWriter html, String results, String selection, int option) {
		html.createHTML(results, selection, option);
		System.out.print("View results now? Enter y for yes or any other key to continue: ");
		String view = console.next();
		if (view.equalsIgnoreCase("y"))
			html.displayHTML(selection);
	}
}