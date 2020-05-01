<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">

<title>Update Entry</title>

<link rel="stylesheet" type="text/css" href="css/styles.css">
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Roboto|Ubuntu">
</head>

<body>

	<div id="container" class="container">
		<h2>Covid19 Tracking App</h2>
		<h3>Update Entry</h3>
				<h4 style="color:red"> Entry was not updated. Fields cannot be blank.</h4>
		

		<div style="clear: both:"></div>
		<p>

			<c:url var="temp" value="ResultControllerServlet">
				<c:param name="command" value="LIST" />
				<c:param name="resultID" value="${THE_RESULT.id}"/>
			</c:url>
			<a href="${temp}"> Back to List</a>
		</p>
	</div>
</body>
</html>