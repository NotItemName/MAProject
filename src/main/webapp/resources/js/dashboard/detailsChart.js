function prepareDetailsChart(canvas, data) {
	var chart = {};

	chart.width = canvas.width();
	chart.height = canvas.height();

	// STYLE: change this data to modify chart look

	// STYLE FOR TEXT: all the text on chart will have these same properties
	chart.style = {};
	var style = chart.style;
	style.text = {
		font : "sans",
		"font-size" : 12
	};

	// STYLE FOR GRID: grid is displayed as lines with text on the left
	style.grid = {
		padding : {
			top : 20,
			right : 10,
			bottom : 10,
			left : 50
		},
		// determines the top line value, so there will be N lines above axis
		numberOfLines : 6,
		// determines how far 0,1,2,3,4,5,6 will be off the grid lines
		textOffset : 5,
		// determines used colors
		color : {
			line : "#AAA",
			text : "#555"
		}
	};
	style.grid.height = 0.55 * chart.height;
	style.grid.width = chart.width - style.grid.padding.right
			- style.grid.padding.left;

	// STYLE FOR VALUES
	style.chart = {
		color : {
			satisfaction : "#05A",
			prioritization : "#0A0"
		},
		// determines the width of one value column
		width : 10,
		// determines how many pixels of space are between satisfaction and
		// prioritization columns
		space : 3,
		// determines animation time in ms and easing in Raphael style
		animationTime : 0,
		animationEasing : "<>",
		// determines an angle to rotate the name of criteria
		textAngle : -45,
		// determines how many pixels are between name of criteria and bottom
		// line of grid
		textOffset : 10
	};

	// STYLE FOR FOOTER: style for little block at bottom where Satisfaction and
	// Prioritization labels are shown
	style.footer = {
		padding : {
			bottom : 5
		},
		// determines spacing between Satisfaction and Prioritization blocks
		blockSpace : 20,
		square : {
			// determines side size of colored square
			size : 10,
			// determines spacing between square and text
			padding : 3
		}
	};

	// NEXT CODE IS FOR CHART GENERATION
	chart.paper = Raphael(canvas.get(0), chart.width, chart.height);
	var paper = chart.paper;

	var lineXStart = style.grid.padding.left;
	var lineXWidth = style.grid.width;
	var lineYTop = style.grid.padding.top;
	var lineYBottom = style.grid.height - style.grid.padding.bottom;
	var lineSpacing = (lineYBottom - lineYTop) / style.grid.numberOfLines;
	for ( var line = 0; line <= style.grid.numberOfLines; line++) {
		var y = lineYBottom - (line * lineSpacing);
		// generates "M##,##l##,0" string with X, Y start and width of line
		var pathString = "M" + lineXStart + "," + y + "l" + lineXWidth + ",0";
		var path = paper.path(pathString);
		path.attr("stroke", style.grid.color.line);

		var label = paper.text(0, 0, line);
		label.attr("text-anchor", "start");
		label.attr(style.text);
		label.attr("fill", style.grid.color.text);
		var box = label.getBBox();
		label.attr("x", lineXStart - box.width - style.grid.textOffset);
		label.attr("y", y);
	}
	chart.lineYBottom = lineYBottom;
	chart.lineSpacing = lineSpacing;

	// draw footer
	var x = chart.width / 2;

	var leftText = paper.text(0, 0, data.satisfaction);
	leftText.attr("text-anchor", "start");
	leftText.attr(style.text);
	var box = leftText.getBBox();
	var y = chart.height - style.footer.padding.bottom - box.height / 2;
	var leftTextX = x - box.width - style.footer.blockSpace / 2;
	leftText.attr("x", leftTextX);
	leftText.attr("y", y);

	var leftSquare = paper.rect(0, 0, style.footer.square.size,
			style.footer.square.size);
	leftSquare.attr({
		fill : style.chart.color.satisfaction,
		stroke : style.chart.color.satisfaction,
		x : leftTextX - style.footer.square.size - style.footer.square.padding,
		y : y - style.footer.square.size / 2
	});

	var rightText = paper.text(0, 0, data.prioritization);
	rightText.attr("text-anchor", "start");
	rightText.attr(style.text);
	var rightTextX = x + style.footer.blockSpace / 2 + style.footer.square.size
			+ style.footer.square.padding;
	rightText.attr("x", rightTextX);
	rightText.attr("y", y);

	var rightSquare = paper.rect(0, 0, style.footer.square.size,
			style.footer.square.size);
	rightSquare
			.attr({
				fill : style.chart.color.prioritization,
				stroke : style.chart.color.prioritization,
				x : rightTextX - style.footer.square.size
						- style.footer.square.padding,
				y : y - style.footer.square.size / 2
			});

	chart.drawnObjects = [];

	chart.draw = drawDetailsChart;
	chart.clear = clearDetailsChart;

	return chart;
}

function drawDetailsChart(data) {
	var chart = this;
	var style = chart.style;
	chart.clear();

	// determines width or space between two blocks
	style.chart.blockWidth = style.grid.width / data.length;

	var index = 0;
	for (index in data) {
		var dataPiece = data[index];

		var x = style.grid.padding.left + style.chart.blockWidth * index
				+ style.chart.blockWidth / 2;
		var y = chart.lineYBottom;

		// draw columns
		var leftX = x - style.chart.width - style.chart.space / 2;
		var rightX = x + style.chart.space / 2;
		var leftColumn = chart.paper.rect(leftX, y, style.chart.width, 0);
		leftColumn.attr("fill", style.chart.color.satisfaction);
		leftColumn.attr("stroke", style.chart.color.satisfaction);
		leftColumn.animate({
			height : chart.lineSpacing * dataPiece.satisfaction,
			y : y - chart.lineSpacing * dataPiece.satisfaction
		}, style.chart.animationTime, style.chart.animationEasing);
		chart.drawnObjects.push(leftColumn);

		var rightColumn = chart.paper.rect(rightX, y, style.chart.width, 0);
		rightColumn.attr("fill", style.chart.color.prioritization);
		rightColumn.attr("stroke", style.chart.color.prioritization);
		rightColumn.animate({
			height : chart.lineSpacing * dataPiece.prioritization,
			y : y - chart.lineSpacing * dataPiece.prioritization
		}, style.chart.animationTime, style.chart.animationEasing);
		chart.drawnObjects.push(rightColumn);

		// draw text
		var label = chart.paper.text(0, 0, dataPiece.name);
		label.attr("text-anchor", "end");
		label.attr(style.text);
		/* var box = label.getBBox(); */
		var transformString = "t" + x + "," + y;
		transformString += "r" + style.chart.textAngle + ",0,0";
		transformString += "t-" + style.chart.textOffset + ",0";
		label.transform(transformString);
		showObject(label, style.chart.animationTime);
		chart.drawnObjects.push(label);
	}
}

function clearDetailsChart() {
	var chart = this;
	var style = chart.style;
	// clear chart columns
	for ( var index in chart.drawnObjects) {
		hideObject(chart.drawnObjects[index], style.chart.animationTime,
				removeThis);
	}
	chart.drawnObjects = [];
}