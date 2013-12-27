<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>MA</title>
<link rel="stylesheet" type="text/css" href="resources/css/ui-style.css" />
<script type="text/javascript" src="resources/js/jquery-1.9.1.min.js"></script>
<c:set var="js" value="resources/js/dashboard/"></c:set>
<script type="text/javascript" src="${js}raphael-min.js"></script>
<script type="text/javascript" src="${js}raphaelUtils.js"></script>
<script type="text/javascript" src="${js}motivationMap.js"></script>
<script type="text/javascript" src="${js}complexMotivationMap.js"></script>
<script type="text/javascript" src="${js}detailsChart.js"></script>
<script type="text/javascript" src="${js}complexDetailsChart.js"></script>
<script type="text/javascript" src="${js}dashBoardInteractions.js"></script>
<script type="text/javascript" src="resources/js/subordinateTree.js"></script>
<script type="text/javascript">
	var NO_USER_WITH_RESULT = '<fmt:message key="jsp.dashboard.noUsersWithResult"/>';
	var localization = {
		satisfaction : "<fmt:message key = 'jsp.dashboard.satisfaction'/>",
		prioritization : "<fmt:message key = 'jsp.dashboard.prioritization'/>",
		dimensions : [ "<fmt:message key = 'enum.dimension.morale'/>",
				"<fmt:message key = 'enum.dimension.career'/>",
				"<fmt:message key = 'enum.dimension.professional'/>",
				"<fmt:message key = 'enum.dimension.compensation'/>" ],
		colors : questionColors
	};

	var surveyNames = [];
	<c:forEach var="survey" items="${revertedSurveyList}">
	surveyNames.push({
		id : "${survey.id}",
		name : "${survey.description[pageContext.request.locale].name}"
	});
	</c:forEach>
</script>
</head>
<body>
	<t:header page="dashboard" title="jsp.dashboard.title" />

	<div class="content">
		<div class="leftBox">
			<div class="label">
				<fmt:message key="jsp.dashboard.finishedSurveys" />
			</div>
			<div class="surveysBox">
				<t:finishedSurveyList />
			</div>
			<div class="label">
				<fmt:message key="jsp.dashboard.chooseSubordinates" />
			</div>
			<div class="subordinatesBox"></div>
		</div>

		<div class="rightBox">
			<div class="label">
				<fmt:message key="jsp.dashboard.motivationMap" />
			</div>
			<%@include file="/WEB-INF/jspf/dashboardMotivationMap.jspf"%>
			<%@include file="/WEB-INF/jspf/dashboardComplexMotivationMap.jspf"%>
			<div class="label">
				<fmt:message key="jsp.dashboard.details" />
				<div class="detailsButton" id="detailsButton">+</div>
			</div>
			<div id="detailsChartsBox">
				<%@include file="/WEB-INF/jspf/dashboardDetailsChart.jspf"%>
				<%@include file="/WEB-INF/jspf/dashboardComplexDetailsChart.jspf"%>
			</div>
		</div>
	</div>
</body>
</html>