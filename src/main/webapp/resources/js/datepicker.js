$(datepickerInit);

function datepickerInit() {
	if ($(".surveyDate .disabled").size() > 0) {
		return;
	}
	datepickerProperties = {
		minDate : 0,
		showOn : "button",
		buttonImage : "resources/images/jquery-ui/calendar.gif",
		buttonImageOnly : true
	};

	$("#surveyStartDate, #surveyEndDate").datepicker(datepickerProperties)
			.datepicker("option", $.datepicker.regional[curentLocale])
			.datepicker("option", "dateFormat", "yy-mm-dd");
	$("#surveyStartDate, #surveyEndDate").click(function() {
		$(this).datepicker('show');
	});
}