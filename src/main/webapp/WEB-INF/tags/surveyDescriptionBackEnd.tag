<jsp:directive.tag language="java" pageEncoding="utf-8" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<form id="surveyCreation">
	<c:forEach var="locale" items="${langs}">
		<c:set var="description" value="${survey.description[locale]}" />
		<input id="survey_name_${locale}" type="text"
			name="survey_name_${locale}"
			<c:if test="${not empty description}"> value="${description.name}" readonly</c:if> />
		<br />
		<textarea id="survey_satisfaction_${locale}"
			name="survey_satisfaction_${locale}" rows="1" cols="20"
			<c:if test="${not empty description}">readonly</c:if>>
			<c:if test="${not empty description}">${description.satisfaction}</c:if>
		 </textarea>
		<textarea id="survey_prioritization_${locale}"
			name="survey_prioritization_${locale}" rows="1" cols="20"
			<c:if test="${not empty description}">readonly</c:if>>
			<c:if test="${not empty description}">${description.prioritization}</c:if> 
		</textarea>
	</c:forEach>

</form>