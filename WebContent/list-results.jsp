<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Covid19 50 States Tracker</title>
<!--link rel="stylesheet" type="text/css" href="/css/styles.css"-->

<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.19/css/dataTables.bootstrap4.min.css" />
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Roboto|Ubuntu">
<style>
.centerF {
	margin: auto;
	width: 150px;
	/*border-style: solid;
	border-width: thick;*/
}

h1, h2, h3, h4, h5, h6 {
	font-family: Ubuntu, sans-serif;
	text-align: center;
	color: #1f3d3d;
}
</style>
</head>

<body
	style="font-family: Ubuntu, sans-serif; background-color: #ffffff;">

	<div id="wrapper">
		<div id="header"></div>

		<div id="container" style="background-color: #ffffff;">

			<div id="content" style="background-color: #ffffff;">
				<!--c:set var="selectedState" value="${state}" scope="request" /-->


				<div class="container mb-3 mt-3"
					style="background-color: #f2f9f9; border-style: solid; border-width: thin; border-radius: 5px;">
					<p>
					<h1 align=center>

						<c:set var="chosen_state" value="${RESULT_LIST[0]['state']}"
							scope="session" />
						Covid19 Tracking in
						<c:out value="${chosen_state}" />, United States
						<!-- %=request.getParameter("state").toUpperCase()%-->
					</h1>

					<input type="button" value="Add Entry"
						onclick="window.location.href='add-entry-form.jsp';return false;"
						class="">

					<!--h1 >Selected State: ${state}</h1-->

					<div class="centerF"
						style="border-style: solid; border-width: thin; width: 150px; border-color: rgb(200, 200, 200);">
						<img
							style="border-style: solid; border-width: thin; width: 150px; border-color: rgb(200, 200, 200);"
							class="flag_im"
							src="<%=request.getContextPath()%>/imagesxy/<c:out value="${fn:toLowerCase(chosen_state)}" />.png">

					</div>

					<table class="table table-striped table-bordered text-center"
						style="width: 100%; background-color: #ffffff;" id="mydatatable">
						<thead>
							<tr>
								<th>Date</th>
								<th>State</th>
								<th>Positive Cases</th>
								<th>Hospitalizations</th>
								<th>Deaths</th>
								<th>Action</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="tempResult" items="${RESULT_LIST}">
								<c:url var="temp" value="ResultControllerServlet">
									<c:param name="command" value="LOAD" />
									<c:param name="resultID" value="${tempResult.id}" />
								</c:url>

								<c:url var="deleteLink" value="ResultControllerServlet">
									<c:param name="command" value="DELETE" />
									<c:param name="resultID" value="${tempResult.id}" />
								</c:url>

								<tr>
									<td>${tempResult.date}</td>
									<td>${tempResult.state}</td>
									<td><fmt:formatNumber type="number"
											value="${tempResult.positive}" /></td>
									<td><c:if test="${tempResult.hospitalizations == 0 }"> NA
									</c:if> <c:if test="${tempResult.hospitalizations != 0 }">
											<fmt:formatNumber type="number"
												value="${tempResult.hospitalizations}" />
										</c:if></td>
									<td><fmt:formatNumber type="number"
											value="${tempResult.death}" /></td>
									<td><a href="${temp}">Edit</a> | <a href="${deleteLink}"
										onclick="if(!(confirm('Are you sure you want to delete this entry?'))) return false">Delete</a></td>
								</tr>
							</c:forEach>


						</tbody>
					</table>
					
		<div style="margin-top: 12px; height: 40px;"> 
		<form style="float:left" action="ResultControllerServlet" method="GET">
			<input type="hidden" name="command" value="RESET"> 
						<input type="submit" value="Reset" class="">
					</form>
					
		<form style="float:right;" action="ResultControllerServlet" method="GET">
			<input type="hidden" name="command" value="EXIT"> 
						<input type="submit" value="Exit" class="">
					</form>
					</div>
					<p>
				</div>
			</div>
		</div>
	</div>
</body>
<footer style="font-size: small; display:block;">
	<h6>Created by Rachel Friedman for CISC 4800 | Project 2 | April
		2020</h6>
</footer>
<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
<script
	src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>

<script
	src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
<script
	src="https://cdn.datatables.net/1.10.19/js/dataTables.bootstrap4.min.js"></script>

<script>
	$("#mydatatable").DataTable({
		pageLength : 10,
		filter : true,
		deferRender : true,
		scrollY : 600,
		scrollCollapse : true,
		scroller : true,
		ordering : true,
		select : true,
	});
</script>


</html>