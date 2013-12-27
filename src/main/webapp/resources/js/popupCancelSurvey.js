function popupCancel() {
	showPopup(popupMessages["cancel_survey"], [ {
		name : popupMessages["yes"],
		handler : cancelSurvey
	}, {
		name : popupMessages["no"]
	} ]);
}
