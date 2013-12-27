$(initAddQuestion);
function initAddQuestion() {
	$("#addNewQuestion").click(showForm);
	$("#cancelAddQuestionButton").click(showButton);
	$("#saveAddQuestionButton").click(addQuestion);
}
function showForm() {
	$("#addNewQuestion").hide(500);
	$("#surveyAddQuestionForm").show(500);
}
function showButton() {
	$("#surveyAddQuestionForm").hide(500);
	$("#addNewQuestion").show(500);
}
function addQuestion() {
	var data = {};
	data.type = $("#newQuestionDimension").val();
	for ( var i = 0; i < locales.length; i++) {
		var locale = locales[i];
		var nameBox = $("#newQuestionName" + locale);
		var satisfactionTextBox = $("#newQuestionSatisfactionText" + locale);
		var prioritizationTextBox = $("#newQuestionPrioritizationText" + locale);
		data["name" + locale] = nameBox.val();
		data["satisfactionText" + locale] = satisfactionTextBox.val();
		data["prioritizationText" + locale] = prioritizationTextBox.val();
		nameBox.val("");
		satisfactionTextBox.val("");
		prioritizationTextBox.val("");
	}
	$.ajax({
		url : "addQuestion",
		type : "POST",
		data : data,
		dataType : "json",
		success : function(data) {
			if (data.error) {
				$("#addQuestionErrorMessage").show();
				return;
			}
			$("#addQuestionErrorMessage").hide();
			var number = $("#surveyQuestionListBox").find(".surveyQuestionBox")
					.size() + 1;

			var newBox = $("#emptyQuestionBox").clone();
			newBox.addClass("surveyQuestionBox");
			newBox.attr("id", "surveyQuestionBox" + data.id);
			newBox.find("#emptyNumber").attr("id", null).text(number);
			newBox.find("#emptyDimension").attr("id", null).text(
					dimensions[data.type]);
			for ( var i = 0; i < locales.length; i++) {
				var locale = locales[i];
				var name = data.name[i];
				var satisfactionText = data.satisfactionText[i];
				var prioritizationText = data.prioritizationText[i];
				newBox.find("#emptyName" + locale).text(name).attr("id",
						"name" + locale + data.id);
				newBox.find("#emptySatisfactionText" + locale).text(
						satisfactionText).attr("id",
						"satisfactionText" + locale + data.id);
				newBox.find("#emptyPrioritizationText" + locale).text(
						prioritizationText).attr("id",
						"prioritizationText" + locale + data.id);
			}

			newBox.find("#emptyEditButton").attr("onclick",
					"editQuestion(" + data.id + ")").attr("id", null);
			newBox.find("#emptyDeleteButton").attr("onclick",
					"deleteQuestion(" + data.id + ")").attr("id", null);

			$("#surveyQuestionListBox").append(newBox);
			showQuestionAddedPopup();
			showButton();
		}
	});
}
function showQuestionAddedPopup() {
	var message = popupMessages.questionAdded;
	var handlers = [];
	handlers.push({
		name : "Ok"
	});
	showPopup(message, handlers);
}