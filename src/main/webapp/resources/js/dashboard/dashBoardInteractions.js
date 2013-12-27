var parameters = [ "satisfaction", "prioritization" ];
var dimensions = [ "morale", "career", "professional", "compensation" ];
var questionColors = [ "#E71914", "#21329F", "#10B910", "#E7A714", "#0C8B8B",
		"#74139A", "#BCDF13", "#8E0000", "#0F005F", "#156300", "#8A7800" ];
var dimensionColors = [ "#d11245", "#e7b414", "#5fcc12", "#2823a1" ];
$(function() {
	map = prepareMotivationMap($("#motivationMap"), localization);
	chart = prepareDetailsChart($("#detailsChart"), localization);
	complexMap = prepareComplexMotivationMap($("#complexMotivationMap"),
			localization, dimensionColors);
	complexChart = prepareComplexDetailsChart($("#complexDetailsChart"),
			localization, questionColors);
	initDetailsBoxListener($("#detailsButton"), $("#detailsChartsBox"));

	$("#complexMotivationMapBox").hide();
	$("#complexDetailsChartBox").hide();
});
function initDetailsBoxListener(switchButton, targetCanvas) {
	var hidden = true;
	targetCanvas.hide();
	switchButton.text("+");
	switchButton.click(function() {
		hidden = !hidden;
		if (hidden) {
			targetCanvas.hide();
			switchButton.text("+");
		} else {
			targetCanvas.show();
			switchButton.text("-");
		}
	});
}
function redrawCharts() {
	var surveysId = new Array();
	var selectedSurveysNames = new Array();
	$('input[name="surveyId"]:checked').each(function() {
		surveysId.push($(this).val());
	});
	surveyNames.forEach(function(value) {
		if ($.inArray(value.id, surveysId) > -1) {
			selectedSurveysNames.push(value.name);
		}
	});
	var userId = new Array();
	$("input[name='userId']:checked").each(function() {
		userId.push($(this).val());
	});
	if (surveysId.length == 0 || userId.length == 0) {
		map.clear();
		chart.clear();
		complexMap.clear();
		complexChart.clear();
		$("#complexMotivationMapDimensionControls").hide();
		return;
	}
	$.ajax({
		url : "chartData",
		data : {
			"userId" : userId,
			"surveyId" : surveysId
		},
		dataType : "json",
		success : function(chartData) {
			if (chartData.surveysNumber == 1) {
				$("#complexMotivationMapBox").hide();
				$("#complexDetailsChartBox").hide();
				$("#motivationMapBox").show();
				$("#detailsChartBox").show();
				map.fill(chartData.motivationData);
				resetMap();
				chart.draw(chartData.detailsData);
			} else {
				$("#motivationMapBox").hide();
				$("#detailsChartBox").hide();

				var motivationData = new Array();
				var complexDetailsChartData = new Array();
				var index = 0;
				surveysId.reverse();
				surveysId.forEach(function(id) {
					var motivationValue = null;
					var detailsValue = null;
					chartData.motivationData.forEach(function(value) {
						if (value.surveyId == id) {
							motivationValue = value;
							detailsValue = chartData.detailsData[index++];
						}
					});
					motivationData.push(motivationValue);
					complexDetailsChartData.push(detailsValue);
				});

				$("#complexDetailsChartControls").empty();
				var colorIndex = 0;
				for ( var id in chartData.questions) {
					var box = $("#questionBoxToClone").clone();
					var input = box.find("#questionInputToClone");
					var name = box.find("#questionNameToClone");

					box.attr("id", null);
					input.attr("id", id);
					name.attr("id", null);
					name.css("color", questionColors[colorIndex]);
					var text = chartData.questions[id];
					name.text(text);
					$("#complexDetailsChartControls").append(box);
					box.show();

					colorIndex++;
					if (colorIndex == questionColors.length) {
						colorIndex = 0;
					}
				}
				$("#complexDetailsChartControls").find("input").first().attr(
						"checked", "checked");

				complexMap.drawGrid(selectedSurveysNames);
				complexMap.fillData(motivationData);
				complexMap.showAllDimensions();
				$("#complexMotivationMapBox").show();

				complexChart.drawGrid(selectedSurveysNames);
				complexChart.fillData(complexDetailsChartData);
				resetComplexChart();
				$("#complexDetailsChartBox").show();
			}
		},
		traditional : true
	});
}
function resetMap() {
	var showParameters = [];
	for ( var index in parameters) {
		showParameters.push(isChecked("map" + parameters[index]));
	}
	map.show(showParameters);
}
function resetComplexMap() {
	var showParameters = [];
	var showDimensions = [];
	for ( var index in parameters) {
		showParameters.push(isChecked("complexMap" + parameters[index]));
	}
	for ( var index in dimensions) {
		showDimensions.push(isChecked("complexMap" + dimensions[index]));
	}
	complexMap.showDimensions(showParameters, showDimensions);
}
function resetComplexChart() {
	var showParameters = [];
	var checkedQuestions = [];
	$("#complexDetailsChartControls").find("input:checked").each(function() {
		checkedQuestions.push($(this).attr("id"));
	});
	for ( var index in parameters) {
		showParameters
				.push(isChecked("complexDetailsChart" + parameters[index]));
	}
	complexChart.showQuestions(showParameters, checkedQuestions);
	// complexChart.showQuestions(checkedQuestions);
}
function isChecked(id) {
	return $("#" + id).is(':checked');
}