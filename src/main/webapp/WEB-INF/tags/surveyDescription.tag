<jsp:directive.tag language="java" pageEncoding="utf-8" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ attribute name="surveyType" required="true" type="String"%>

<c:forEach var="surveyDescription"
	items="${currentSurveyForEmployee.description}">
	<c:if
		test="${surveyDescription.key eq pageContext.request.locale.language}">
		<c:choose>
			<c:when test="${surveyType eq 'satisfaction'}">
				${surveyDescription.value.satisfaction}
			</c:when>
			<c:otherwise>
				${surveyDescription.value.prioritization}
			</c:otherwise>
		</c:choose>
	</c:if>
</c:forEach>