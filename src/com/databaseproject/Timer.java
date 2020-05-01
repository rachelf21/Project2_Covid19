package com.databaseproject;

import java.text.DecimalFormat;

/**
 * Helper class to time how long various methods take to run
 * 
 * @author Rachel Friedman
 * @version 1.0
 */
public class Timer {
	long read_start = System.nanoTime();
	long read_end = System.nanoTime();
	long now = System.nanoTime();

	public long start() {
		return System.nanoTime();
	}

	public long stop() {
		return System.nanoTime();
	}
	
	/**
	 * Calculates the difference between starting time and ending time
	 * @param start the time the method was started
	 * @param stop the time the method stopped
	 * @return the total running time in milliseconds
	 */
	
	public String calculateRunningTime(long start, long stop) {
		DecimalFormat decimalFormat = new DecimalFormat("###.###");
		String result = decimalFormat.format((stop - start) / Math.pow(10, 6)) + " milliseconds";
		return result;
	}
}
