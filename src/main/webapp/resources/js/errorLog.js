$(printPagination);

function printPagination() {
	var pagination = $(".pagination");

	var currentPage = $("#currentPage").val();
	var totalPageAmount = $("#totalPageAmount").val();

	var i = 0;
	var dots = false;
	while (i < totalPageAmount) {
		i++;
		if (i == 1 || (i - 1) == 1 || currentPage == i
				|| (i - currentPage) == 1 || (currentPage - i) == 1
				|| (totalPageAmount - i) == 1 || totalPageAmount == i) {
			if (i == currentPage) {
				pagination.append("<span class='paginationPageCurrent'>" + i
						+ "</span>");
			} else {
				pagination.append("<span class='paginationPage'>" + i
						+ "</span>");
			}
		} else {
			if (!dots) {
				dots = true;
				pagination.append("<span class = 'paginationDots'>...</span>");
			}
		}
	}
	
	$(".paginationPage").click(changePage);
}

function changePage(event) {
	var src = $(event.target) ? $(event.target) : $(event.srcElement);
	var pageNumber = src.text();

	var url = window.location;

	if (url.search.length == 0) {
		url.search = "currentPage=" + pageNumber;
	} else if (url.search.indexOf("currentPage") == -1)
		url.search = url.search + "&currentPage=" + pageNumber;
	else {
		var newPage = "currentPage=" + pageNumber;
		url.search = url.search.replace(/currentPage=\d{0,3}/, newPage);
	}
}
