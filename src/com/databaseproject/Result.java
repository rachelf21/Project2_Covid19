package com.databaseproject;

import java.util.Date;
/**
 * This class represents the results returned by the database query
 * @author Rachel Friedman
 *
 */
public class Result {
	
	private int id;
	private java.sql.Date date;
	private String state;
	private int positive;
	private int hospitalizations;
	private int death;

	/**
	 * Creates a Result object. Each Result object represents one record returned by the database query.
	 * @param date date of report
	 * @param state state of report
	 * @param positive number of positive cases reported in selected state on given day
	 * @param hospitalizations number of hospitalizations reported in selected state on given day
	 * @param death number of deaths reported in selected state on given day
	 */
	public Result(Date date, String state, int positive, int hospitalizations, int death) {
		this.date = (java.sql.Date) date;
		this.state = state;
		this.positive = positive;
		this.hospitalizations = hospitalizations;
		this.death = death;
	}

	/**
	 * Creates a Result object with a specified ID. Each Result object represents one record returned by the database query.
	 * @param id the ID (primary key) of the Result object (database record)
	 * @param date date of report
	 * @param state state of report
	 * @param positive number of positive cases reported in selected state on given day
	 * @param hospitalizations number of hospitalizations reported in selected state on given day
	 * @param death number of deaths reported in selected state on given day
	 */
	public Result(int id, Date date, String state, int positive, int hospitalizations, int death) {
		this.id = id;
		this.date = (java.sql.Date) date;
		this.state = state;
		this.positive = positive;
		this.hospitalizations = hospitalizations;
		this.death = death;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public java.sql.Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = (java.sql.Date) date;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getPositive() {
		return positive;
	}

	public void setPositive(int positive) {
		this.positive = positive;
	}

	public int getHospitalizations() {
		return hospitalizations;
	}

	public void setHospitalizations(int hospitalizations) {
		this.hospitalizations = hospitalizations;
	}

	public int getDeath() {
		return death;
	}

	public void setDeath(int death) {
		this.death = death;
	}

	@Override
	public String toString() {
		return "Result [id=" + id + ", date=" + date + ", state=" + state + ", positive=" + positive
				+ ", hospitalizations=" + hospitalizations + ", death=" + death + "]";
	}

}
