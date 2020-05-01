<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, com.databaseproject.*"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Covid 19 Tracker App</title>
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.19/css/dataTables.bootstrap4.min.css" />
</head>

<%
	//get the resuls from the request object sent by servlet

	List<Result> theResults = (List<Result>) request.getAttribute("RESULT_LIST");
%>
<body>

	<%=theResults%>


	<div id="wrapper">
		<div id="header">
			<div id="container">
				<div id="content">
					<div class="container mb-5 mt-5">
			<h2 align="center">Results from abc New York</h2>

						<table class="table table-striped table-bordered text-center"
							style="width: 100%;" id="mydatatable">
							<thead>
								<tr>
									<th>id</th>
									<th>Date</th>
									<th>State</th>
									<th>Positive Cases</th>
									<th>Hospitalizations</th>
									<th>Deaths</th>
								</tr>
							</thead>
							<tbody>
								<%
									for (Result tempResult : theResults) {
								%>
								<tr>
									<td><%=tempResult.getId()%></td>
									<td><%=tempResult.getDate()%></td>
									<td><%=tempResult.getState()%></td>
									<td><%=tempResult.getPositive()%></td>
									<td><%=tempResult.getHospitalizations()%></td>
									<td><%=tempResult.getDeath()%></td>
								</tr>
								<%
									}
								%>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>

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
</body>
</html>