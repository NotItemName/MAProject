$(document).ready(function() {
	$(".minus").click(minusCoin);
	$(".plus").click(plusCoin);
});
	
var budget = 0;

function minusCoin(){
	var moneyBox = $(this).parent();
	var valueBox = moneyBox.children(".value");
	var hiddenField = moneyBox.children("input:hidden");
	var hiddenValue = parseInt(hiddenField.val());
	if (hiddenValue > 0) {
		hiddenValue--;
		hiddenField.val(hiddenValue);
		drawCoins(valueBox, hiddenValue);
		
		budget++;
		budgetBox = $(".budgetBox .value");
		drawCoins(budgetBox, budget);
	}
}

function plusCoin(){
	var moneyBox = $(this).parent();
	var valueBox = moneyBox.children(".value");
	var hiddenField = moneyBox.children("input:hidden");
	var hiddenValue = parseInt(hiddenField.val());
	if (hiddenValue < 6 && budget > 0) {
		hiddenValue++;
		hiddenField.val(hiddenValue);
		drawCoins(valueBox, hiddenValue);
		
		budget--;
		budgetBox = $(".budgetBox .value");
		drawCoins(budgetBox, budget);
	}
}

function drawCoins(valueBox, value) {
	valueBox.find("div").each(function(index) {
		var coinBox = $(this);
		if (index < value) {
			coinBox.removeClass("emptyCoin");
			coinBox.addClass("coin");
			coinBox.text("O");
		} else {
			coinBox.addClass("emptyCoin");
			coinBox.removeClass("coin");
			coinBox.html("&nbsp;");
		}
	});
}