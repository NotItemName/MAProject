<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>MA</title>
<link rel="stylesheet" type="text/css" href="resources/css/style.css" />
<link rel="stylesheet" type="text/css" href="resources/css/ui-style.css" />
<link rel="stylesheet" type="text/css"
	href="resources/css/jquery-ui-1.10.3.custom.css" />
<script src="resources/js/jquery-1.9.1.min.js" type="text/javascript"></script>
<script src="resources/js/deleteQuestion.js" type="text/javascript"></script>
<script src="resources/js/editQuestion.js" type="text/javascript"></script>
<script src="resources/js/addQuestion.js" type="text/javascript"></script>
<script src="resources/js/scheduleSurvey.js" type="text/javascript"></script>
<script src="resources/js/stopSurvey.js" type="text/javascript"></script>
<script src="resources/js/cancelSurvey.js" type="text/javascript"></script>
<script src="resources/js/popupStopSurvey.js" type="text/javascript"></script>
<script src="resources/js/popupCancelSurvey.js" type="text/javascript"></script>
<script src="resources/js/subordinateTree.js" type="text/javascript"></script>
<script src="resources/js/sendLink.js" type="text/javascript"></script>
<script src="resources/js/ui/jquery-ui-1.10.3.custom.js"
	type="text/javascript"></script>
<script src="resources/js/ui/jquery.ui.datepicker-en.js"
	type="text/javascript"></script>
<script src="resources/js/ui/jquery.ui.datepicker-ru.js"
	type="text/javascript"></script>
<script src="resources/js/datepicker.js" type="text/javascript"></script>
<script type="text/javascript">
	var locales = [];
	<c:forEach var="locale" items="${locales}">
	locales.push("${locale}");
	</c:forEach>

	var curentLocale = "${pageContext.request.locale}";

	var surveyStatusesMap = {};
	surveyStatusesMap["SCHEDULED"] = '<fmt:message key="enum.surveyStatus.scheduled" />';
	surveyStatusesMap["IN_PROGRESS"] = '<fmt:message key="enum.surveyStatus.inProgress" />';
	surveyStatusesMap["FINISHED"] = '<fmt:message key="enum.surveyStatus.finished" />';

	dimensions = [];
	dimensions["MORALE"] = "<fmt:message key='enum.dimension.morale' />";
	dimensions["CAREER"] = "<fmt:message key='enum.dimension.career' />";
	dimensions["PROFESSIONAL"] = "<fmt:message key='enum.dimension.professional' />";
	dimensions["COMPENSATION"] = "<fmt:message key='enum.dimension.compensation' />";

	var stopAlertCancelButton = "<fmt:message key='jsp.createSurvey.list.survey.button.cancel'/>";
	var stopAlertOKButton = "<fmt:message key='jsp.createSurvey.surveyHeader.button.ok' />";
	var stopAlertMessage = "<fmt:message key='jsp.createSurvey.surveyHeader.message.stop' />";
	var cantEditQuestionMessage = "<fmt:message key='jsp.createSurvey.list.question.cantEdit' /> ";

	var popupMessages = [];
	popupMessages["schedule_survey"] = '<fmt:message key = "jsp.createSurvey.popup.scheduleSurvey"/>';
	popupMessages["yes"] = '<fmt:message key = "jsp.createSurvey.popup.yes"/>';
	popupMessages["no"] = '<fmt:message key = "jsp.createSurvey.popup.no"/>';
	popupMessages["close"] = '<fmt:message key = "jsp.createSurvey.popup.close"/>';
	popupMessages["cancel_survey"] = '<fmt:message key="jsp.createSurvey.popup.cancelSurvey" />';
	popupMessages["stop_survey"] = '<fmt:message key="jsp.createSurvey.popup.stopSurvey" />';
	popupMessages["cant_delete_question"] = '<fmt:message key="jsp.createSurvey.popup.cantDeleteQuestion"/>';
	popupMessages["questionAdded"] = '<fmt:message key="jsp.createSurvey.popup.questionAdded"/>';

	var errorMessages = [];
	errorMessages["title"] = '<fmt:message key="jsp.createSurvey.errorMessages.title"/>';
	errorMessages["start_date_invalid"] = '<fmt:message key = "jsp.createSurvey.errorMessages.startDateInvalid"/>';
	errorMessages["end_date_invalid"] = '<fmt:message key = "jsp.createSurvey.errorMessages.endDateInvalid"/>';
	errorMessages["name_invalid"] = '<fmt:message key = "jsp.createSurvey.errorMessages.surveyNameEmpty"/>';
	errorMessages["satisfaction_invalid"] = '<fmt:message key = "jsp.createSurvey.errorMessages.descriptionEmpty"/>';
	errorMessages["prioritization_invalid"] = '<fmt:message key = "jsp.createSurvey.errorMessages.descriptionEmpty"/>';
	errorMessages["date_interval_invalid"] = '<fmt:message key = "jsp.createSurvey.errorMessages.dateIntervalInvalid"/>';
	errorMessages["empty_question_list"] = '<fmt:message key = "jsp.createSurvey.errorMessages.questionListEmpty"/>';

	var popupSendLinkMessages = [];
	popupSendLinkMessages["subordinate_tree"] = ${subordinateTree};
	popupSendLinkMessages["div"] = "<div class='subordinatesBox'></div>";
	popupSendLinkMessages["send"] = "<fmt:message key='jsp.createSurvey.popup.sendLink'/>";
	popupSendLinkMessages["cancel"] = "<fmt:message key='jsp.createSurvey.popup.cancel'/>";
</script>

</head>
<body>
	<t:popup />
	<t:header page="createSurvey" title="jsp.createSurvey.title" />
	<div class="content">
		<div class="singleBox">
			<%@include file="/WEB-INF/jspf/surveyForm.jspf"%>
			<%@include file="/WEB-INF/jspf/surveyDescriptions.jspf"%>
			<%@include file="/WEB-INF/jspf/surveyQuestionList.jspf"%>
		</div>
	</div>
</body>
</html>