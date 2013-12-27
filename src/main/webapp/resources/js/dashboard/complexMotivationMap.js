function prepareComplexMotivationMap(canvas, data, colors) {
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
		},
		// determines overlap on dashed vertial lines
		lineOffset : 5,
		// determines dashed line style
		dash : "--"
	};
	style.grid.height = 0.55 * chart.height;
	style.grid.width = chart.width - style.grid.padding.right
			- style.grid.padding.left;

	// STYLE FOR VALUES
	style.chart = {
		color : colors,
		dash : {
			satisfaction : "",
			prioritization : "- "
		},
		// determines the width of one value column
		width : 2,
		// determines animation time in ms and easing in Raphael style
		animationTime : 0,
		animationEasing : "<>",
		// determines an angle to rotate the name of surveys
		textAngle : -45,
		// determines how many pixels are between name of survey and bottom
		// line of grid
		textOffset : 10,
		radius : 4
	};

	// STYLE FOR FOOTER: style for little block at bottom where Satisfaction and
	// Prioritization labels are shown
	style.footer = {
		padding : {
			bottom : 5
		},
		// determines spacing between Satisfaction and Prioritization blocks
		blockSpace : 7,
		line : {
			// determines side size of colored line
			width : 20,
			// determines spacing between line and text
			padding : 2
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
	var footer = paper.set();
	var x = 0;

	var parameters = [ "satisfaction", "prioritization" ];
	for ( var index in parameters) {
		var parameter = parameters[index];
		var line = drawLine(paper, style.footer.line.width, x,
				style.chart.width, style.chart.color[parameter]);
		line.attr("stroke-dasharray", style.chart.dash[parameter]);
		footer.push(line);
		x += style.footer.line.width + style.footer.line.padding;

		var text = drawText(paper, data[parameter], x, style.text);
		footer.push(text);
		x += text.getBBox().width + style.footer.blockSpace;
	}

	for ( var index in data.dimensions) {
		var dimension = data.dimensions[index];
		var dimensionLine = drawLine(paper, style.footer.line.width, x,
				style.chart.width, style.chart.color.dimension);
		dimensionLine.attr("stroke", style.chart.color[index]);

		footer.push(dimensionLine);
		x += style.footer.line.width + style.footer.line.padding;

		var dimensionText = drawText(paper, dimension, x, style.text);
		footer.push(dimensionText);
		x += dimensionText.getBBox().width + style.footer.blockSpace;
	}

	var footerX = chart.width / 2 - footer.getBBox().width / 2;
	var footerY = chart.height - style.footer.padding.bottom
			- footer.getBBox().height / 2;
	footer.translate(footerX, footerY);

	chart.drawnGridObjects = [];
	chart.drawnDataObjects = [];

	chart.drawGrid = drawComplexMotivationGrid;
	chart.fillData = fillComplexMotivationMap;
	chart.clearGrid = clearComplexMotivationGrid;
	chart.showDimensions = showDimensions;
	chart.showAllDimensions = showDimensions;
	chart.clear = clearComplexMotivationMap;

	return chart;
}

function drawLine(paper, width, x, strokeWidth, strokeColor) {
	var line = paper.path("M" + x + ",0l" + width + ",0");
	line.attr("stroke-width", strokeWidth);
	line.attr("stroke", strokeColor);
	return line;
}

function drawText(paper, string, x, style) {
	var text = paper.text(x, 0, string);
	text.attr("text-anchor", "start");
	text.attr(style);
	return text;
}

function drawComplexMotivationGrid(data) {
	var chart = this;
	var style = chart.style;
	chart.clearGrid();
	chart.clear();

	// determines width or space between two blocks
	style.chart.blockWidth = style.grid.width / data.length;

	var index = 0;
	for (index in data) {
		var dataPiece = data[index];

		var x = style.grid.padding.left + style.chart.blockWidth * index
				+ style.chart.blockWidth / 2;
		var y = chart.lineYBottom;

		// draw vertical lines
		var startY = y + style.grid.lineOffset;
		var lineHeight = chart.lineSpacing * style.grid.numberOfLines
				+ style.grid.lineOffset * 2;
		var pathString = "M" + x + "," + startY + "l0,-" + lineHeight;
		var line = chart.paper.path(pathString);
		line.attr("stroke-dasharray", style.grid.dash);
		line.attr("stroke", style.grid.color.line);
		chart.drawnGridObjects.push(line);

		// draw text
		var label = chart.paper.text(0, 0, dataPiece);
		label.attr("text-anchor", "end");
		label.attr(style.text);
		/* var box = label.getBBox(); */
		var transformString = "t" + x + "," + y;
		transformString += "r" + style.chart.textAngle + ",0,0";
		transformString += "t-" + style.chart.textOffset + ",0";
		label.transform(transformString);
		showObject(label, style.chart.animationTime);
		chart.drawnGridObjects.push(label);
	}
}

function fillComplexMotivationMap(data) {
	var chart = this;
	var style = chart.style;
	chart.clear();

	var stepX = style.chart.blockWidth;
	var y = chart.lineYBottom;
	var stepY = chart.lineSpacing;
	var parameters = [ "satisfaction", "prioritization" ];
	for ( var dimension = 0; dimension < 4; dimension++) {
		var set = [];
		chart.drawnDataObjects.push(set);

		var x = style.grid.padding.left + style.chart.blockWidth / 2;
		var lastX = [ null, null ];
		var lastY = [ null, null ];
		// draw all data for current dimension
		for ( var index in data) {
			// draw satisfaction and prioritization
			for ( var parameterIndex in parameters) {
				var parameter = parameters[parameterIndex];
				if (set[parameter] == undefined) {
					set[parameter] = chart.paper.set();
				}
				if (data[index] == null || data[index][parameter] == null
						|| data[index][parameter][dimension] == null) {
					continue;
				}
				var level = data[index][parameter][dimension];

				// draw circle
				var circleY = y - level * stepY;
				var circle = chart.paper.circle(x, circleY, style.chart.radius);
				circle.attr("stroke-width", 0);
				circle.attr("fill", style.chart.color[dimension]);
				set[parameter].push(circle);

				// draw line
				if (lastX[parameterIndex] != null) {
					var startY = lastY[parameterIndex];
					var endY = circleY;
					var startX = lastX[parameterIndex];
					var endX = x;
					var pathString = "M" + startX + "," + startY;
					pathString += "L" + endX + "," + endY;
					var line = chart.paper.path(pathString);
					line.attr("stroke-width", style.chart.width);
					line.attr("stroke", style.chart.color[dimension]);
					line.attr("stroke-dasharray", style.chart.dash[parameter]);
					set[parameter].push(line);
				}

				lastX[parameterIndex] = x;
				lastY[parameterIndex] = circleY;
			}
			x += stepX;
		}
		for ( var innerSet in set) {
			set[innerSet].attr("opacity", 0.0);
		}
	}
}

function showDimensions(parameterFlags, dimensions) {
	var chart = this;
	var style = chart.style;
	var parameters = [ "satisfaction", "prioritization" ];
	for ( var dimension in chart.drawnDataObjects) {
		var set = chart.drawnDataObjects[dimension];
		for ( var parameterIndex in parameters) {
			var parameter = parameters[parameterIndex];
			var innerSet = set[parameter];
			if ((dimensions == undefined || dimensions[dimension])
					&& (parameterFlags == undefined || parameterFlags[parameterIndex])) {
				showObject(innerSet, style.chart.animationTime, null, 1.0);
			} else {
				hideObject(innerSet, style.chart.animationTime);
			}
		}
	}
}

function clearComplexMotivationGrid() {
	var chart = this;
	var style = chart.style;
	// clear chart columns
	for ( var index in chart.drawnGridObjects) {
		hideObject(chart.drawnGridObjects[index], style.chart.animationTime,
				removeThis);
	}
	chart.drawnGridObjects = [];
}

function clearComplexMotivationMap() {
	var chart = this;
	var style = chart.style;
	// clear chart lines
	var parameters = [ "satisfaction", "prioritization" ];
	for ( var index in chart.drawnDataObjects) {
		for ( var parameterIndex in parameters) {
			hideObject(
					chart.drawnDataObjects[index][parameters[parameterIndex]],
					style.chart.animationTime, removeThis);
		}
	}
	chart.drawnDataObjects = [];
}