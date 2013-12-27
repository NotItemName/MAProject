$(function() {
	$("input[name='surveyId']").change(createTreeOfEmployeesWithResult);
});

function createTreeOfEmployeesWithResult() {
	map.clear();
	chart.clear();
	complexMap.clear();
	complexChart.clear();
	var surveysId = new Array();
	$("input[name='surveyId']:checked").each(function(){
		surveysId.push($(this).val());
	});
	if(surveysId.length == 0){
		$(".subordinatesBox").html("");
		return;
	}
	$(".subordinatesBox").html("");
	$.ajax({
		type : "POST",
		url : "surveysUsers",
		data : {
			surveysId : surveysId
		},
		dataType : "json",

		success : function(data) {
			if(data.error == true){
				$(".subordinatesBox").html(NO_USER_WITH_RESULT);
				return;
			}
			for(var name in data){
				createSubordinateDiv(name, data[name], 0);
			}
			$(".subordinatesBox").append('<div class="clear"></div>');
			treeChecker();
			$(".subordinate input:checkbox").change(function () {
				redrawCharts();
			});
		},
		traditional : true
	});
}

function createTreeOfEmployees(data) {
	for(var name in data){
		createSubordinateDiv(name, data[name], 0);
	}
	$(".subordinatesBox").append('<div class="clear"></div>');
}

function createSubordinateDiv(name, unit, level){
	if(level == 0){
		$(".subordinatesBox").append("<div class='level" + level + "'></div>");
	}
	
	var users = unit['users'];
	var units = unit['units'];
	
	$(".level" + level + ":last").append('<div class="subordinate"></div>')
						 .find(".subordinate:last")
						 .append('<input class="checkbox" type="checkbox" />')
						 .append('<div class="name">' + name + '</div>')
						 .append("<div class='level" + (level + 1) + "'></div>");
	
	for(var id in users){
		$(".level" + (level + 1) + ":last").append('<div class="subordinate"></div>')
							 .find(".subordinate:last")
							 .append('<input class="checkbox" type="checkbox" name="userId" value="'+ id +'"/>')
							 .append('<div class="name">' + users[id] + '</div>');					 
	}
	
	for(var name in units){
		createSubordinateDiv(name, units[name], level+1);
	}	
}

function treeChecker() {
	$.extend($.expr[':'], {
		unchecked: function (obj) {
			return ((obj.type == 'checkbox' || obj.type == 'radio') && !$(obj).is(':checked'));
		}
	});

	$(".subordinate input:checkbox").change(checkbox);
}

function checkbox(event, first) {
	if(!first){
		$(this).next().next('div').find('input:checkbox').prop('checked', $(this).prop("checked"));
	}
	var ret = true;
	$(this).parent().parent().children(".subordinate").each(function(){
		if(!$(this).find('input:checkbox').prop('checked')){
			ret = false;
			return false;
		}
	});
	var target = $(this).parent().parent().prev().prev();
	if(ret){
		$(target).prop('checked',true);
	} else {
		$(target).prop('checked',false);
	};
	
	$(target).trigger('change', true);
}