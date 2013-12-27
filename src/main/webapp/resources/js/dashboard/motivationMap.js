function prepareMotivationMap(canvas, data) {
	var map = {};

	map.width = canvas.width();
	map.height = canvas.height();

	// STYLE: change this data to modify chart look

	// STYLE FOR TEXT: all the text on chart will have these same properties
	map.style = {};
	style = map.style;
	style.text = {
		font : "sans",
		"font-size" : 12
	};

	// STYLE FOR GRID: grid is displayed as lines with text on the left
	style.grid = {
		padding : {
			top : 30,
			right : 30,
			bottom : 60,
			left : 30
		},
		// determines how many lines should it draw
		numberOfLines : 6,
		// determines how far 0,1,2,3,4,5,6 will be off the grid lines
		textOffset : 5,
		// determines used colors
		color : {
			line : "#AAA",
			text : "#555"
		}
	};
	style.grid.height = map.height - style.grid.padding.top
			- style.grid.padding.bottom;
	style.grid.width = map.width - style.grid.padding.right
			- style.grid.padding.left;

	// STYLE FOR VALUES
	style.map = {
		color : {
			satisfaction : "#05A",
			prioritization : "#0A0"
		},
		// determines animation time in ms and easing in Raphael style
		animationTime : 0,
		animationEasing : "<>",
		// determines the width of lines
		width : 1.5
	};

	// STYLE FOR FOOTER: style for little block at bottom where Satisfaction and
	// Prioritization labels are shown
	style.footer = {
		padding : {
			bottom : 5
		},
		// determines spacing between Satisfaction and Prioritization blocks
		blockSpace : 20,
		line : {
			// determines side size of colored line
			width : 20,
			// determines spacing between line and text
			padding : 3
		}
	};

	// NEXT CODE IS FOR MAP GENERATION
	var paper = Raphael(canvas.get(0), map.width, map.height);
	map.paper = paper;

	var gridX = style.grid.padding.left + style.grid.width / 2;
	var gridY = style.grid.padding.top + style.grid.height / 2;
	var gridSpace = 0;
	if (style.grid.width > style.grid.height) {
		gridSpace = style.grid.height / 2 / style.grid.numberOfLines;
	} else {
		gridSpace = style.grid.width / 2 / style.grid.numberOfLines;
	}
	map.gridX = gridX;
	map.gridY = gridY;
	map.gridSpace = gridSpace;
	var zeroLabel = paper.text(0, 0, "0");
	zeroLabel.attr(style.text);
	zeroLabel.attr("fill", style.grid.color.text);
	zeroLabel.attr("x", gridX);
	zeroLabel.attr("y", gridY);
	for ( var lineIndex = 1; lineIndex <= style.grid.numberOfLines; lineIndex++) {
		// draw lines
		var offset = gridSpace * lineIndex;
		var points = [ {
			x : gridX,
			y : gridY - offset
		}, {
			x : gridX + offset,
			y : gridY
		}, {
			x : gridX,
			y : gridY + offset
		}, {
			x : gridX - offset,
			y : gridY
		} ];
		var pathString = "";
		var operation = "M";
		for ( var index = 0; index < points.length; index++) {
			var point = points[index];
			pathString += operation + point.x + "," + point.y;
			operation = "L";
		}
		pathString += "z";
		var path = paper.path(pathString);
		path.attr("stroke", style.grid.color.line);

		// draw numbers
		var label = paper.text(0, 0, lineIndex);
		label.attr(style.text);
		label.attr("fill", style.grid.color.text);
		label.attr("x", gridX);
		var box = label.getBBox();
		label.attr("y", gridY - offset + box.height / 2);

		// draw text for dimensions
		if (lineIndex == style.grid.numberOfLines) {
			var dimensionLabels = [];
			for ( var index = 0; index < points.length; index++) {
				var point = points[index];
				var label = paper.text(0, 0, data.dimensions[index]);
				label.attr(style.text);
				label.attr("fill", style.grid.color.text);
				label.attr("x", point.x);
				label.attr("y", point.y);
				dimensionLabels[index] = label;
			}
			// 0 - top, 1 - right, 2 - bottom, 3 - left
			var box = dimensionLabels[0].getBBox();
			var transformString = "t0,"
					+ (-box.height / 2 - style.grid.textOffset);
			dimensionLabels[0].transform(transformString);

			box = dimensionLabels[1].getBBox();
			transformString = "t" + (box.width / 2 + style.grid.textOffset)
					+ ",0";
			dimensionLabels[1].transform(transformString);

			box = dimensionLabels[2].getBBox();
			transformString = "t0," + (box.height / 2 + style.grid.textOffset);
			dimensionLabels[2].transform(transformString);

			box = dimensionLabels[3].getBBox();
			transformString = "t" + (-box.width / 2 - style.grid.textOffset)
					+ ",0";
			dimensionLabels[3].transform(transformString);
		}
	}

	// draw footer
	var x = map.width / 2;

	var leftText = paper.text(0, 0, data.satisfaction);
	leftText.attr("text-anchor", "start");
	leftText.attr(style.text);
	var box = leftText.getBBox();
	var y = map.height - style.footer.padding.bottom - box.height / 2;
	var leftTextX = x - box.width - style.footer.blockSpace / 2;
	leftText.attr("x", leftTextX);
	leftText.attr("y", y);

	var leftLineStartX = (leftTextX - style.footer.line.width - style.footer.line.padding);
	var leftLinePathString = "M" + leftLineStartX + "," + y;
	leftLinePathString += "l" + style.footer.line.width + ",0";
	var leftLine = paper.path(leftLinePathString);
	leftLine.attr("stroke", style.map.color.satisfaction);
	leftLine.attr("stroke-width", style.map.width);

	var rightText = paper.text(0, 0, data.prioritization);
	rightText.attr("text-anchor", "start");
	rightText.attr(style.text);
	var rightTextX = x + style.footer.blockSpace / 2 + style.footer.line.width
			+ style.footer.line.padding;
	rightText.attr("x", rightTextX);
	rightText.attr("y", y);

	var rightLineStartX = (rightTextX - style.footer.line.width - style.footer.line.padding);
	var rightLinePathString = "M" + rightLineStartX + "," + y;
	rightLinePathString += "l" + style.footer.line.width + ",0";
	var rightLine = paper.path(rightLinePathString);
	rightLine.attr("stroke", style.map.color.prioritization);
	rightLine.attr("stroke-width", style.map.width);

	map.fill = fillMotivationMap;
	map.show = showMotivationMap;
	map.clear = clearMotivationMap;

	return map;
}

function getMapPathString(levels, gridX, gridY, gridSpace) {
	var mapPoints = [ {
		x : 0,
		y : -1
	}, {
		x : 1,
		y : 0
	}, {
		x : 0,
		y : 1
	}, {
		x : -1,
		y : 0
	} ];
	var pathString = "";
	var operation = "M";
	for ( var index = 0; index < 4; index++) {
		var level = levels[index];
		var point = mapPoints[index];
		var pointX = (gridX + point.x * level * gridSpace);
		var pointY = (gridY + point.y * level * gridSpace);
		pathString += operation + pointX + "," + pointY;
		operation = "L";
	}
	return pathString += "z";
}

function fillMotivationMap(data) {
	var map = this;
	var style = map.style;
	map.clear();
	var same = true;
	for ( var index in data.satisfaction) {
		if (data.satisfaction[index] != data.prioritization[index]) {
			same = false;
			break;
		}
	}

	// draw map lines
	map.satisfactionLine = map.paper.path(getMapPathString(data.satisfaction,
			map.gridX, map.gridY, map.gridSpace));
	map.satisfactionLine.attr("stroke", style.map.color.satisfaction);
	map.satisfactionLine.attr("stroke-width", style.map.width);
	map.satisfactionLine.attr("opacity", 0.0);

	map.prioritizationLine = map.paper.path(getMapPathString(
			data.prioritization, map.gridX, map.gridY, map.gridSpace));
	map.prioritizationLine.attr("stroke", style.map.color.prioritization);
	map.prioritizationLine.attr("stroke-width", style.map.width);
	map.prioritizationLine.attr("opacity", 0.0);
	if (same) {
		map.prioritizationLine.attr("stroke-width", style.map.width * 2);
		map.prioritizationLine.attr("stroke-dasharray", "- ");
	}
}

function showMotivationMap(parameterFlags) {
	if (parameterFlags[0]) {
		showObject(map.satisfactionLine, style.map.animationTime, null, 1.0);
	} else {
		hideObject(map.satisfactionLine, style.map.animationTime);
	}
	if (parameterFlags[1]) {
		showObject(map.prioritizationLine, style.map.animationTime, null, 1.0);
	} else {
		hideObject(map.prioritizationLine, style.map.animationTime);
	}
}

function clearMotivationMap() {
	var map = this;
	var style = map.style;
	// clear map lines
	if (map.satisfactionLine) {
		hideObject(map.satisfactionLine, style.map.animationTime, removeThis);
	}
	if (map.prioritizationLine) {
		hideObject(map.prioritizationLine, style.map.animationTime, removeThis);
	}
}