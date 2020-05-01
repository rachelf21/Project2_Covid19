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
		<div style="float:right">
			<img src="<%=request.getContextPath()%>/imagesxy/update.png">
		</div>
		<h2>Covid19 Tracking App</h2>
		<h3>
			Update Entry for
			<c:out value="${chosen_state}" />
		</h3>

		<div style="margin: auto; width: 150px">

			<img
				style="border-style: solid; border-width: thin; width: 150px; border-color: rgb(200, 200, 200);"
				class="flag_im"
				src="<%=request.getContextPath()%>/imagesxy/<c:out value="${fn:toLowerCase(chosen_state)}" />.png">

		</div>

		<form action="ResultControllerServlet" method="GET">

			<input type="hidden" name="command" value="UPDATE"> <input
				type="hidden" name="resultId" value="${THE_RESULT.id}">

			<div class="row">
				<div class="col-25">
					<label for="date">Date </label>
				</div>
				<div class="col-75">
					<input readonly type="date" id="date" name="date"
						value="${THE_RESULT.date}">
				</div>
			</div>

			<div class="row">
				<div class="col-25">
					<label for="state">State </label>
				</div>
				<div class="col-75">
					<input readonly type="text" id="state" name="state"
						value="${THE_RESULT.state}">
				</div>
			</div>

			<div class="row">
				<div class="col-25">
					<label for="state">Positive Cases </label>
				</div>
				<div class="col-75">
					<input type="number" id="positive" name="positive"
						value="${THE_RESULT.positive}">
				</div>
			</div>

			<div class="row">
				<div class="col-25">
					<label for="state">Hospitalizations </label>
				</div>
				<div class="col-75">
					<input type="number" id="hosp" name="hosp"
						value="${THE_RESULT.hospitalizations}">
				</div>
			</div>

			<div class="row">
				<div class="col-25">
					<label for="state">Deaths </label>
				</div>
				<div class="col-75">
					<input type="number" id="death" name="death"
						value="${THE_RESULT.death}">
				</div>
			</div>
			<p>
			<div class="row">
				<input type="submit" value="Save" id="save" class="save">
			</div>

		</form>


		<div style="clear: both:"></div>
		<p>

			<c:url var="temp" value="ResultControllerServlet">
				<c:param name="command" value="LIST" />
				<c:param name="resultID" value="${THE_RESULT.id}" />
			</c:url>
			<a href="${temp}"> Back to List</a>
		</p>
	</div>
</body>
</html>