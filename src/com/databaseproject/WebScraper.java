package com.databaseproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class is used to retrieve data from a specified website.
 * 
 * @author Rachel Friedman
 * @version 2.0 This version utilizes Java Servlets and JSP
 *
 */
public class WebScraper {

	Timer timer = new Timer();

	/**
	 * Retrieves data from website specified in urlAddress file.
	 * 
	 * @param urlAddress the website to retrieve data from
	 * @return the content from that website
	 */
	public byte[] retrieveDataFromWebsite(String urlAddress) {
		StringBuffer stringBuffer = new StringBuffer();
		byte[] data = null;
		try {
			URL url = new URL(urlAddress);
			long start = timer.start();
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			System.out.println("Retrieving data from " + urlAddress.substring(0, 25) + "...");
			InputStream inputStream = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			int i;
			while ((i = reader.read()) != -1) {
				char c = (char) i;
				if (c == '\n') {
					stringBuffer.append("\n");
				}
				else {
					stringBuffer.append(String.valueOf(c));
				}
			}
			reader.close();
			long end = timer.stop();
			String time = timer.calculateRunningTime(start, end);
			System.out.println("Data retrieved successfully in " + time + ".");
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			data = stringBuffer.toString().getBytes();
		}
		return data;
	}

	/**
	 * Saves data to a specified csv file
	 * 
	 * @param filename the name and location of the csv file
	 * @param data     the data to be saved to the file
	 * @return the name of each column header along with its data type. This string can then be used in an SQL statement when creating a table.
	 */
	public String saveToFile(String filename, byte[] data) {
		File file = new File(filename);
		ArrayList<String> columnHeaders = new ArrayList<String>();
		String columnHeadersWithDataTypes = "";
		long start = timer.start();
		try (FileOutputStream fop = new FileOutputStream(file)) {
			if (!file.exists()) {
				file.createNewFile();
			}
			fop.write(data);
			fop.flush();
			fop.close();
			long end = timer.stop();
			String time = timer.calculateRunningTime(start, end);
			System.out.println("Data saved successfully in " + time + ".");

			columnHeaders = getHeaderRow(filename);
			for (int i = 0; i < columnHeaders.size(); i++) {
				if (i == 0 || i == 16 ||  i==21)
					columnHeaders.set(i, columnHeaders.get(i) + " date"); //date, dateModified,dateChecked
				else if (i == 1 || i == 6 || i == 15 || i == 17 ||  i == 21 || i == 46 || i == 49)
					columnHeaders.set(i, columnHeaders.get(i) + " text"); //state, totalTestResultsSource,lastUpdateEt, dataQualityGrade (now blank), hash
				else
					columnHeaders.set(i, columnHeaders.get(i) + " integer");
				if (i < columnHeaders.size() - 1)
					columnHeadersWithDataTypes = columnHeadersWithDataTypes + columnHeaders.get(i) + ", ";
				else
					columnHeadersWithDataTypes = columnHeadersWithDataTypes + columnHeaders.get(i);
			}
			// columnHeadersWithDataTypes = "ID serial primary key not null, " +
			// columnHeadersWithDataTypes;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return columnHeadersWithDataTypes;
	}

	/**
	 * Helper method to aid in retrieving header row from csv file
	 * 
	 * @param filename the csv file with the header row
	 * @return all column names in header row (data types not included)
	 */
	private static ArrayList<String> getHeaderRow(String filename) {
		ArrayList<String> header = new ArrayList<String>();
		try {
			Scanner data = new Scanner(new File(filename));
			String[] line = data.nextLine().split(",");
			for (int i = 0; i < line.length; i++) {
				header.add(line[i]);
				// System.out.print(line[i] + ", ");
			}
			data.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("Missing file " + e.getMessage());
		}
		catch (NumberFormatException e) {
			System.out.println("invalid number");
		}
		catch (Exception e) {
			System.out.println("An error has occured " + e.getMessage());
		}
		return header;
	}

}
