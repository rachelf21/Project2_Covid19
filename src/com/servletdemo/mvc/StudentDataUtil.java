package com.servletdemo.mvc;

import java.util.ArrayList;
import java.util.List;

public class StudentDataUtil {

	public static List<Student> getStudents() {
		List<Student> students = new ArrayList<>();
		students.add(new Student("Mary", "Public", "mary@gmail.com"));
		students.add(new Student("Bob", "Smith", "bob@gmail.com"));
		students.add(new Student("Sally", "Jackson", "sally@gmail.com"));
		return students;
	}
}
