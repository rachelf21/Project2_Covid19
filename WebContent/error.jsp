<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">

<title>Add Entry</title>

<link rel="stylesheet" type="text/css" href="css/styles.css">
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Roboto|Ubuntu">

</head>

<body>

	<div id="container" class="container">
			<div style="float:right; width: 100px">
			<img src="<%=request.getContextPath()%>/imagesxy/add.png">
		</div>
		<h2>Covid19 Tracking App</h2>
		<h3>Add Entry for <c:out value="${chosen_state}" /> </h3>
				<div style="margin: auto; width: 150px">

			<img
				style="border-style: solid; border-width: thin; width: 150px; border-color: rgb(200, 200, 200);"
				class="flag_im"
				src="<%=request.getContextPath()%>/imagesxy/<c:out value="${fn:toLowerCase(chosen_state)}" />.png">

		</div>
		<h4 style="color:red"> Entry was not added. Fields cannot be blank.</h4>
		<form action="ResultControllerServlet" method="GET">

			<input type="hidden" name="command" value="ADD"> <input
				type="hidden" name="resultId" value="${THE_RESULT.id}">
			<div class="row">
				<div class="col-25">
					<label for="date">Date </label>
				</div>
				<div class="col-75">
					<input type="date" id="date" name="date">
				</div>
			</div>

			<div class="row">
				<div class="col-25">
					<label for="state">State </label>
				</div>
				<div class="col-75">
					<input type="text" id="state" name="state" readonly
						value="${chosen_state}">
				</div>
			</div>

			<div class="row">
				<div class="col-25">
					<label for="state">Positive Cases </label>
				</div>
				<div class="col-75">
					<input type="number" id="positive" name="positive">
				</div>
			</div>

			<div class="row">
				<div class="col-25">
					<label for="state">Hospitalizations </label>
				</div>
				<div class="col-75">
					<input type="number" id="hosp" name="hosp">
				</div>
			</div>


			<div class="row">
				<div class="col-25">
					<label for="state">Deaths </label>
				</div>
				<div class="col-75">
					<input type="number" id="death" name="death">
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

<script>
	let today = new Date().toISOString().substr(0, 10);
	document.querySelector("#date").value = today;
</script>
</html>