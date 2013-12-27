function stopSurvey() {
	$.ajax({
		type : "POST",
		url : "stopSurvey",
		dataType : "json",

		success : function(data) {
			if (data.surveyStatus == 'FINISHED') {
				
				$('#stopSurveyButton').hide(500);
				$('#cancelSurveyButton').hide(500);
				$('#scheduleSurveyButton').show(500);

				$('.inputs input').each(function() {
					this.className = "input";
					this.disabled = false;
					this.value = "";
				})

				$('.surveyDate input').each(function() {
					this.className = "input";
					this.disabled = false;
					this.value = "yyyy-mm-dd";
				});

				$('.surveyStatus').hide(500);				
				$('.surveyLink #surveyLinkLabel').hide(500);
				$('.surveyLink #surveyLink').hide(500);
				
				$('.surveyDescriptionsForm textarea').each(function() {
					this.className = "textArea";
					this.disabled = false;
					this.value = "";					
				})
				$("#sendLinkButton").hide(500);
				$("#addNewQuestion").show(500);
				$("#surveyStartDate, #surveyEndDate").removeAttr("disabled");
				datepickerInit();
			}
		}
	});

}
