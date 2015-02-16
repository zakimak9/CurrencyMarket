<%@page import="java.util.List"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="req" value="${pageContext.request}" />
<c:set var="url">${req.requestURL}</c:set>
<c:set var="uri" value="${req.requestURI}" />
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Message Frontend</title>
<base
	href="${fn:substring(url, 0, fn:length(url) - fn:length(uri))}${req.contextPath}/">
<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="js/jquery.flot.min.js"></script>
<script type="text/javascript" src="js/jquery.flot.time.js"></script>
<script type="text/javascript" src="js/jshashtable-2.1.js"></script>
<script type="text/javascript"
	src="js/jquery.numberformatter-1.2.3.min.js"></script>
<script type="text/javascript" src="js/jquery.flot.symbol.js"></script>
<script type="text/javascript" src="js/jquery.flot.axislabels.js"></script>
<script type="text/javascript" src="js/jquery-jvectormap-2.0.1.min.js"></script>
<script type="text/javascript"
	src="js/jquery-jvectormap-world-mill-en.js"></script>

<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css"
	href="css/jquery-jvectormap-2.0.1.css">

</head>
<%
	// Page will be auto refresh after 5 seconds
	//response.setIntHeader("Refresh", 10);

	//Get Current Time
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Calendar cal = Calendar.getInstance();
%>
<body>
	<h1>Visualize Currency Market</h1>
	<div id="container">

		<div id="content">
			<font size="3" color="red"><b>Last updated at: <%=dateFormat.format(cal.getTime())%>
					BDT
			</b></font><br /> <br />
			<div id="tableContainer" style="height: 700px; overflow: auto">
				The page will update automatically every 10 seconds. <br />The below selection can
				be used to filter Currency data (From&#8594;To) with time of placement
				<form id="queryForm" action="${req.contextPath}/display" method="GET">
					<select name="currencyWatch">
						<option value="All" selected>All</option>
						<c:forEach var="entry" items="${allKeys}">
							<c:set var="entryKey">${entry}</c:set>
							<%
								if (pageContext.getAttribute("entryKey").equals(
											request.getParameter("currencyWatch"))) {
							%>
							<option value="${entry}" selected>${entry.replaceAll("->","&#8594;").replaceAll("mapping:","")}</option>
							<%
								} else {
							%>
							<option value="${entry}">${entry.replaceAll("->","&#8594;").replaceAll("mapping:","")}</option>
							<%
								}
							%>

						</c:forEach>
					</select> 
					<select name="dateWatch">
						<option value="All" selected>All</option>
						<c:forEach var="entry" items="${allDates}">
							<c:set var="entryKey">${entry}</c:set>
							<%
								if (pageContext.getAttribute("entryKey").equals(
											request.getParameter("dateWatch"))) {
							%>
							<option value="${entry}" selected>${entry.replaceAll("date:","")}</option>
							<%
								} else {
							%>
							<option value="${entry}">${entry.replaceAll("date:","")}</option>
							<%
								}
							%>

						</c:forEach>
					</select>
					<select name="showAvgOnly">
							<%
								if ("true".equals(request.getParameter("showAvgOnly"))) {
							%>
							<option value="true" selected>Show Averages Only</option>
							<option value="false">Show All Related Entries</option>
							<%
								} else {
							%>
							<option value="false" selected>Show All Related Entries</option>
							<option value="true">Show Averages Only</option>
							<%
								}
							%>
					</select>
					
				</form>
				<br />
				<c:forEach var="entry" items="${keys}">
					<u><b>${entry.key.replaceAll("->","&#8594;")}</b></u>
					<br />
					<br />
					<%
						if ("true".equals(request.getParameter("showAvgOnly"))) {
					%>
					<div class="infotable" style="display:none">
					<%
						}else {
					%>
					<div class="infotable">
					<%
						}
					%>
						<table>
							<tbody>
								<c:set var="avgRate" value="0" />
								<c:set var="total" value="${entry.value.size()}" />
								<tr>
									<td>userId</td>
									<td>amountSell</td>
									<td>amountBuy</td>
									<td>rate</td>
									<td>timePlaced</td>
									<td>originatingCountry</td>
								</tr>
								<c:forEach var="value" items="${entry.value}">
									<tr>
										<td>${value.split(",")[0]}</td>
										<td>${value.split(",")[1]}</td>
										<td>${value.split(",")[2]}</td>
										<td>${value.split(",")[3]}</td>
										<td>${value.split(",")[4]}</td>
										<td>${value.split(",")[5]}</td>
										<c:set var="avgRate" value="${avgRate + value.split(',')[3]}" />
									</tr>
								</c:forEach>
							</tbody>
						</table>

					</div>
					
					<c:if test="${avgRate/total != 'NaN'}">
						<br />
						<div style="color:darkgreen">
							<b>Average Rate: <fmt:formatNumber
									value="${avgRate/total}"
									maxFractionDigits="3" /></b>
						</div>
					</c:if>
					<br />
				</c:forEach>
			</div>
		</div>

		<div id="flot-placeholder"></div>
	</div>
	<div id="world-map" style="width: 600px; height: 400px; float: right"></div>

	<script>
		$(function() {
			var gdpData =
	<%=request
					.getAttribute("originRequestCountStringForMapView")%>
		;
			$('#world-map').vectorMap(
					{
						map : 'world_mill_en',
						series : {
							regions : [ {
								values : gdpData,
								scale : [ '#C8EEFF', '#0071A4' ],
								normalizeFunction : 'polynomial'
							} ]
						},
						onRegionTipShow : function(e, el, code) {
							el.html(el.html() + ' (Placement - '
									+ gdpData[code] + ')');
						}
					});

			// check if local storage is supported
			if (typeof (Storage) !== "undefined") {

				if (localStorage.verticalScroll) {
					$('#tableContainer').scrollTop(localStorage.verticalScroll)
				}

				// reload page after 10
				setTimeout(function() {
					// save scroll position
					var verticalScroll = $('#tableContainer').scrollTop();
					localStorage.setItem("verticalScroll", verticalScroll);
					$("#queryForm").submit();
				}, 10000);

			}
		});
	</script>
</body>

<script>
	var data = [
<%=request.getAttribute("originRequestCountStringForView")%>
	];

	var dataset = [ {
		label : "Total Placements",
		data : data,
		color : "#5482FF"
	} ];

	var ticks = [
<%=request.getAttribute("countriesLabel")%>
	];

	var options = {
		series : {
			bars : {
				show : true
			}
		},
		bars : {
			align : "center",
			barWidth : 0.5
		},
		xaxis : {
			axisLabel : "Countries of Origin",
			axisLabelUseCanvas : true,
			axisLabelFontSizePixels : 12,
			axisLabelFontFamily : 'Verdana, Arial',
			axisLabelPadding : 10,
			ticks : ticks

		},
		yaxis : {
			axisLabel : "Placements",
			axisLabelUseCanvas : true,
			axisLabelFontSizePixels : 12,
			axisLabelFontFamily : 'Verdana, Arial',
			axisLabelPadding : 3,
			tickFormatter : function(v, axis) {
				return v;
			}
		},
		legend : {
			noColumns : 0,
			labelBoxBorderColor : "#000000",
			position : "nw"
		},
		grid : {
			hoverable : true,
			borderWidth : 2,
			backgroundColor : {
				colors : [ "#ffffff", "#EDF5FF" ]
			}
		}
	};

	$(document).ready(function() {
		$.plot($("#flot-placeholder"), dataset, options);
		$("#flot-placeholder").UseTooltip();
	});

	function gd(year, month, day) {
		return new Date(year, month, day).getTime();
	}

	var previousPoint = null, previousLabel = null;

	$.fn.UseTooltip = function() {
		$(this).bind(
				"plothover",
				function(event, pos, item) {
					if (item) {
						if ((previousLabel != item.series.label)
								|| (previousPoint != item.dataIndex)) {
							previousPoint = item.dataIndex;
							previousLabel = item.series.label;
							$("#tooltip").remove();

							var x = item.datapoint[0];
							var y = item.datapoint[1];

							var color = item.series.color;

							//console.log(item.series.xaxis.ticks[x].label);                

							showTooltip(item.pageX, item.pageY, color,
									"<strong>" + item.series.label
											+ "</strong><br>"
											+ item.series.xaxis.ticks[x].label
											+ " : <strong>" + y + "</strong>");
						}
					} else {
						$("#tooltip").remove();
						previousPoint = null;
					}
				});
	};

	function showTooltip(x, y, color, contents) {
		$('<div id="tooltip">' + contents + '</div>').css({
			position : 'absolute',
			display : 'none',
			top : y - 40,
			left : x - 120,
			border : '2px solid ' + color,
			padding : '3px',
			'font-size' : '9px',
			'border-radius' : '5px',
			'background-color' : '#fff',
			'font-family' : 'Verdana, Arial, Helvetica, Tahoma, sans-serif',
			opacity : 0.9
		}).appendTo("body").fadeIn(200);
	}
</script>

</html>