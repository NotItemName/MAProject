jQuery.fn.center = function() {
	var top = Math.max(0, (($(window).height() - $(this).outerHeight()) / 2)
			+ $(window).scrollTop());
	this.css("top", top + "px");
	var left = Math.max(0, (($(window).width() - $(this).outerWidth()) / 2)
			+ $(window).scrollLeft());
	this.css("left", left + "px");
	return this;
};

var popupHandlers = [];

function showPopup(message, handlers) {
	$("#popupTextBox").html(message);
	$("#popupButtons .button").each(function() {
		if ($(this).attr("id") != "popupEmptyButton") {
			$(this).remove();
		}
	});
	popupHandlers = [];
	for ( var index in handlers) {
		var handler = handlers[index];
		var button = $("#popupEmptyButton").clone();
		button.attr("id", null);
		button.attr("value", handler.name);
		button.attr("onclick", "popupClick(" + index + ")");
		button.show();
		popupHandlers[index] = handler["handler"];
		$("#popupButtons").append(button);
	}
	$("#popupBox").show();
	$("#popupBackground").show();
	resizePopup();
	$(window).resize(resizePopup);
	$(window).scroll(resizePopup);
}

function popupClick(index) {
	if (popupHandlers[index]) {
		popupHandlers[index]();
	}
	closePopup();
}

function closePopup() {
	$(window).unbind("resize", resizePopup);
	$("#popupBox").hide();
	$("#popupBackground").hide();
}

function resizePopup() {
	var back = $("#popupBackground");
	var padding = parseInt($("body").css("padding-left"));
	back.height($(window).height() + padding);
	back.width($(window).width() + padding);
	back.css("left", -padding + "px");
	back.css("top", (-padding + $(window).scrollTop()) + "px");
	back.css("opacity", "0.2");
	if ($("#popupBox").height() > $(window).height() * 0.75) {
		$("#popupTextBox").css("height", $(window).height() * 0.5 + "px");
		$("#popupTextBox").css("overflow-y", "scroll");
	} else {
		$("#popupTextBox").css("height", "inherit");
		$("#popupTextBox").css("overflow-y", "inherit");
	}
	$("#popupBox").center();
}