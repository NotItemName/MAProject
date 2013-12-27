var datePattern = /^20\d\d-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/;

function popupSchedule(data) {
	var handlers = [];
	handlers.push({
		name : popupMessages["yes"],
		handler : function() {
			doAjax(data);
		}
	});

	handlers.push({
		name : popupMessages["no"]
	});

	showPopup(popupMessages["schedule_survey"], handlers);
}

function scheduleSurvey() {
	for ( var i = 0; i < locales.length; i++) {
		var locale = locales[i];
		$('#surveyName' + locale).removeClass("errorInput");
		$('#surveySatisfactionDescription' + locale).removeClass("errorInput");
		$('#surveyPrioritizationDescription' + locale)
				.removeClass("errorInput");
	}
	$('#surveyStartDate').removeClass("errorInput");
	$('#surveyEndDate').removeClass("errorInput");

	var data = {};
	for ( var i = 0; i < locales.length; i++) {
		var locale = locales[i];
		data["name" + locale] = $('#surveyName' + locale).val();
		data["satisfactionDescription" + locale] = $(
				'#surveySatisfactionDescription' + locale).val();
		data["prioritizationDescription" + locale] = $(
				'#surveyPrioritizationDescription' + locale).val();
	}
	data["startDate"] = $('#surveyStartDate').val();
	data["endDate"] = $('#surveyEndDate').val();

	var errors = validate(data);
	var message = errorCheck(errors);
	if (message != undefined) {
		showErrorPopup(message);
		return;
	} else {
		popupSchedule(data);
	}
}

function doAjax(data) {
	$.ajax({
		type : "POST",
		url : "scheduleSurvey",
		data : data,
		dataType : "json",

		success : function(data) {
			var message = errorCheck(data.errors);
			if (message == undefined) {
				var surveyStatus = data.surveyStatus;
				if (surveyStatus == 'IN_PROGRESS') {
					$("#stopSurveyButton").show(500);
				}
				$("#cancelSurveyButton").show(500);
				$("#scheduleSurveyButton").hide(500);
				$("#surveyLinkLabel").show(500);

				for ( var i = 0; i < locales.length; i++) {
					var locale = locales[i];
					$('#surveyName' + locale).addClass("disabled").attr(
							"disabled", "disabled");
					$('#surveySatisfactionDescription' + locale).addClass(
							"disabled").attr("disabled", "disabled");
					$('#surveyPrioritizationDescription' + locale).addClass(
							"disabled").attr("disabled", "disabled");
				}
				$("#surveyStartDate").addClass("disabled").attr("disabled",
						"disabled");
				$("#surveyEndDate").addClass("disabled").attr("disabled",
						"disabled");
				$("#surveyLink").text(data.link).attr("href", data.link);
				$("#surveyLink").show(500);
				$(".surveyStatus").show(500);
				$("#surveyStatus").html(surveyStatusesMap[surveyStatus]);
				$("#sendLinkButton").show(500);
				$("#addNewQuestion").hide(500);
				$("#surveyStartDate, #surveyEndDate").datepicker("destroy");
			} else {
				showErrorPopup(message);
			}
		}
	});
}

function validate(data) {
	var errors = {};
	var startDate = data.startDate;
	var endDate = data.endDate;
	var dateValid = true;
	if (!datePattern.test(startDate)) {
		errors.surveyStartDate = "start_date_invalid";
		dateValid = false;
	}
	if (!datePattern.test(endDate)) {
		errors.surveyEndDate = "end_date_invalid";
		dateValid = false;
	}
	if (dateValid == true && startDate > endDate) {
		errors.dateInterval = "date_interval_invalid";
	}
	for ( var i = 0; i < locales.length; i++) {
		var locale = locales[i];
		var name = data["name" + locale];
		if (name == undefined || name == "") {
			errors["surveyName" + locale] = "name_invalid";
		}
		var satisfactionDescription = data["satisfactionDescription" + locale];
		if (satisfactionDescription == undefined
				|| satisfactionDescription == "") {
			errors["surveySatisfactionDescription" + locale] = "satisfaction_invalid";
		}
		var prioritizationDescription = data["prioritizationDescription"
				+ locale];
		if (prioritizationDescription == undefined
				|| prioritizationDescription == "") {
			errors["surveyPrioritizationDescription" + locale] = "prioritization_invalid";
		}
	}
	if ($(".surveyQuestionBox").size() < 1) {
		errors.questionList = "empty_question_list";
	}
	return errors;
}

function errorCheck(errors) {
	var message;
	for ( var key in errors) {
		if (message == undefined) {
			message = errorMessages.title;
		}
		errMsg = errorMessages[errors[key]];
		if (message.indexOf(errMsg) == -1) {
			message = message + "<br/>" + errMsg;
		}
		if (key == "dateInterval") {
			$("#surveyStartDate").addClass("errorInput");
			$("#surveyEndDate").addClass("errorInput");
		}
		$("#" + key).addClass("errorInput");
	}
	return message;
}

function showErrorPopup(message) {
	showPopup(message, [ {
		name : "Ok"
	} ]);
}