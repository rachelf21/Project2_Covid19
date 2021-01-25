<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Covid19 50 States Tracker</title>
<link rel="stylesheet" type="text/css" href="css/styles.css">
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Roboto|Ubuntu">
</head>


<body>

	<div class="container">
		<h1>Track Covid-19 throughout the United States</h1>

		<div class="info">
			<p>
				This app tracks the number of positive cases, hospitalizations 
				and deaths across the fifty states in the United States.
				<p>Begin by selecting the state and date range you would like to view.
				Then, if you would like to incorporate data from additional websites, 
				you can customize the initial data by adding, deleting and editing records.
			</p>
		</div>

		<div style="margin: auto; width: 300px">
			<!--img id="logo" width=300 src="http://localhost:8080/Covid19/images/covid_map.png"-->
			<img width=300 src="<%=request.getContextPath()%>/imagesxy/covid_map.png" >
			
		</div>
<p>
		<form action="ResultControllerServlet" method="GET">
			<input type="hidden" name="command" value="CREATE">

			<div class="row">
				<div class="col-25">
					<label for="criteria">State</label>
				</div>
				<div class="col-75">
					<select name="state">
						<c:forEach var="myMap" items="${STATES_ABBR_LIST}">

							<option value="${myMap['key']}"><c:out
									value="${myMap['value']}" /></option>
							<c:if test="${myMap['key'] == 'NY'}">
								<option value="${myMap['key']}" selected><c:out
										value="New York" /></option>
							</c:if>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="row">
				<div class="col-25">
					<label for="start">Starting Date</label>
				</div>
				<div class="col-75">
					<input type="date" id="start" name="start_date">
				</div>
			</div>
			<div class="row">
				<div class="col-25">
					<label for="end">Ending Date</label>
				</div>
				<div class="col-75">
					<input type="date" id="end" name="end_date">
				</div>

			</div>
			<p>
			<div class="row">
				<input type="submit" value="View State Data">
			</div>
		</form>
	</div>


<div style="font-size: small; margin: auto;  width:50%; text-align:center">
	<p >Created by Rachel Friedman for CISC 4800 | Project 2 | April
		2020</p>
	<p>This Project incorporates the <b>CRUD</b> functionalities in a
		relational database.</p>
	<p>Technologies used: Database: <b>PostgreSQL</b>, Backend: <b>Java, JSP,
		JSTL and Servlets</b>, Frontend: <b>HTML, JS, CSS (Bootstrap)</b></p>
				<p>
			Data source: <a href="https://covidtracking.com/"
				target="_blank"> covidtracking.com.</a>
		</p>
		</div>
</body>
<script>
	let today = new Date().toISOString().substr(0, 10);
	document.querySelector("#start").value = "2020-09-01";
	document.querySelector("#end").value = today;
</script>
</html>