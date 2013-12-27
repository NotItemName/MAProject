function popupStop(surveyId) {
	showPopup(popupMessages["stop_survey"], [ {
		name : popupMessages["yes"],
		handler : function() {
			stopSurvey(surveyId);
		}
	}, {
		name : popupMessages["no"]
	} ]);
}