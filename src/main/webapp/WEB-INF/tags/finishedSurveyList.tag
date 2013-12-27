<jsp:directive.tag language="java" pageEncoding="utf-8" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:choose>
	<c:when test="${not empty surveyList}">
		<c:forEach var="survey"  items="${surveyList}">
			<div class="survey">
				<input class="checkbox" type="checkbox" name="surveyId" value="${survey.id}" />
				<div class="name">${survey.description[pageContext.request.locale].name}</div>
			</div>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<fmt:message key="jsp.dashboard.noFinishedSurvey" />
	</c:otherwise>
</c:choose>
