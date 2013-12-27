function popupLinkSend() {
	$(".surveyLink").append("<div id='popupSendLink' hidden='hidden'></div>");
	$("#popupSendLink").append(popupSendLinkMessages["div"]);
	createTreeOfEmployees(popupSendLinkMessages["subordinate_tree"]);
	showPopup($("#popupSendLink").html(), [ {
		name : popupSendLinkMessages["send"],
		handler : sendLink
	}, {
		name : popupSendLinkMessages["cancel"]
	} ]);
	$("#popupSendLink").remove();
	treeChecker();
}

function sendLink() {
	var usersId = new Array();
	$("input[name=\"userId\"]:checked").each(function() {
		usersId.push($(this).val());
	});
	
	if(usersId.length == 0){
		return;
	}
	$.ajax({
		type : "POST",
		url : "sendLink",
		data : {
			"usersId" : usersId
		},
		traditional : true
	});
	$(".subordinatesBox").hide(500);
	$(".subordinatesBox").find("input[type='checkbox']").each(function(){
		$(this).removeAttr('checked');
	});
}
