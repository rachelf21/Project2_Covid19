package com.databaseproject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class States {
	
	static HashMap<String, String> listOfStates = new HashMap<String, String>();
	
	/**
	 * Creates a HashMap of the full name of the abbreviation of each state.<br>
	 * This is used when checking to see if valid state was entered. This is also
	 * used when spelling out full name of state in place of abbreviation.
	 * 
	 * @return a Hashmap of the abbreviations and full names of each of the 50
	 *         states and 6 territories.
	 */
	public static void createListOfStates() {
		try {
			Connection connection = db.getConnection();
			Statement stmt = connection.createStatement();
			String sql = "SELECT ST, state FROM states ORDER BY ST asc;";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String st = rs.getString("st");
				String state = rs.getString("state");
				state = state.replace(" ", "_");
				listOfStates.put(st, state);
			}
			rs.close();
			stmt.close();
		}
		catch (Exception e) {
			System.err.println("createListOfStates " + e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	public static HashMap<String, String> getListOfStates(){
		return listOfStates;
	}
}
