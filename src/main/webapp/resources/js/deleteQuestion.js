function deleteQuestion(id) {
	$.ajax({
		type : "POST",
		url : "deleteQuestion",
		data : {
			questionId : id
		},
		dataType : "json",

		success : function(data) {
			if (data != -1) {
				$('#surveyQuestionBox' + data).hide(500, function() {
					$(this).remove();
				});
			} else {
				showPopup(popupMessages["cant_delete_question"], [ {
					name : "Ok"
				} ]);
			}
		}
	});
}