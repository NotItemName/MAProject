function cancelButton(id) {
	$("#surveyQuestionBox" + id).show(500);
	$('#surveyEditQuestionForm' + id).hide(500, function() {
		$(this).remove();
	});
}

function editQuestion(id) {
	var clone = $('#surveyEditQuestionForm').clone();
	clone.attr("id", "surveyEditQuestionForm" + id);
	for ( var i = 0; i < locales.length; i++) {
		var locale = locales[i];
		clone.find("#editName" + locale).attr("id", "editName" + locale + id);
		clone.find("#editSatisfactionText" + locale).attr("id",
				"editSatisfactionText" + locale + id);
		clone.find("#editPrioritizationText" + locale).attr("id",
				"editPrioritizationText" + locale + id);
	}
	clone.find("#saveAddQuestionButton").attr("onclick",
			"sendEditedQuestion(" + id + ")");
	clone.find("#cancelAddQuestionButton").attr("onclick",
			"cancelButton(" + id + ")");
	var box = $("#surveyQuestionBox" + id);
	box.hide(500);
	box.after(clone);
	clone.show(500);

	for ( var i = 0; i < locales.length; i++) {
		var locale = locales[i];
		var name = $("#name" + locale + id).text();
		var satisfactionText = $("#satisfactionText" + locale + id).text();
		var prioritizationText = $("#prioritizationText" + locale + id).text();

		$('#editName' + locale + id).val(name);
		$('#editSatisfactionText' + locale + id).val(satisfactionText);
		$('#editPrioritizationText' + locale + id).val(prioritizationText);
	}
}

function sendEditedQuestion(id) {
	var data = {};
	data.questionId = id;
	for ( var i = 0; i < locales.length; i++) {
		var locale = locales[i];
		data["name" + locale] = $('#editName' + locale + id).val();
		data["satisfactionText" + locale] = $(
				'#editSatisfactionText' + locale + id).val();
		data["prioritizationText" + locale] = $(
				'#editPrioritizationText' + locale + id).val();
	}
	$
			.ajax({
				type : "POST",
				url : "editQuestion",
				data : data,
				dataType : "json",

				success : function(successFlag) {
					if (!successFlag) {
						var errorBox = $('#surveyEditQuestionForm' + id).find(
								'#errorMessageBox');
						errorBox.text(cantEditQuestionMessage);
						errorBox.css("color", "#f00");
						errorBox.show();
						return;
					}
					var questionBox = $("#surveyQuestionBox" + id);
					questionBox.show(500);
					for ( var i = 0; i < locales.length; i++) {
						var locale = locales[i];
						var name = $('#editName' + locale + id).val();
						var satisfactionText = $(
								'#editSatisfactionText' + locale + id).val();
						var prioritizationText = $(
								'#editPrioritizationText' + locale + id).val();

						$("#name" + locale + id).text(name);
						$("#satisfactionText" + locale + id).text(
								satisfactionText);
						$("#prioritizationText" + locale + id).text(
								prioritizationText);
					}
					$('#surveyEditQuestionForm' + id).hide(500, function() {
						$(this).remove();
					});
				}
			});
}