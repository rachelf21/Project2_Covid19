package com.databaseproject;

import java.util.Date;

public class Result {
	private int id;
	private java.sql.Date date;
	private String state;
	private int positive;
	private int hospitalizations;
	private int death;

	public Result(Date date, String state, int positive, int hospitalizations, int death) {
		this.date = (java.sql.Date) date;
		this.state = state;
		this.positive = positive;
		this.hospitalizations = hospitalizations;
		this.death = death;
	}

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
