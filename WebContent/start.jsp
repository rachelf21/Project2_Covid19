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
<body style="font-family: Ubuntu, sans-serif">
	

			<c:url var="states_listx" value="ResultControllerServlet">
			<c:param name="command" value="LIST_STATES" />
		</c:url>

<div class = "container mb-3 mt-5" style="height: 40px; line-height:10px;">
		<h3>
			<a href="${states_listx}">CLICK TO LAUNCH</a>
		</h3>
		 </div>
		 <p style="margin-bottom: 20px"> &nbsp;</p>
		 <div style="margin: auto; width: 50%; font-size:medium; text-align:center; line-height: 1.6">
		 
		 Launching this app will retrieve the data from <a href="https://covidtracking.com/"
				target="_blank">covidtracking.com</a> and create tables from that data. 
				</div>
				
</body>
</html>