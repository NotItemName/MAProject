// this (your own actually) function should go to onclick="showMyPopup()" on buttons
function showMyPopup() {
	// to add popup to page use <t:popup /> on jsp (in any place within body)
	// you do not need to include any js files (they are included in tag)

	// to use popup call showPopup(message, handlers) function
	// handlers is an array of objects {name:"someName", handler:someFunction}
	// you can leave function empty to make Cancel button (see example below)
	showPopup("Are you sure you want to do something?", [ {
		name : "yes",
		handler : alertYes
	}, {
		name : "nope",
		handler : alertNope
	}, {
		name : "cancel"
	} ]);
}

function alertYes() {
	alert("yes");
}

function alertNope() {
	alert("nope");
}