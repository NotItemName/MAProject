function showObject(object, time, callback, targetOpacity) {
	object.attr("opacity", 0.0);
	object.animate({
		opacity : targetOpacity != null ? targetOpacity : 1.0
	}, time, "linear", callback);
}

function hideObject(object, time, callback) {
	// object.attr("opacity", 1.0);
	object.animate({
		opacity : 0.0
	}, time, "linear", callback);
}

function removeThis() {
	this.remove();
}