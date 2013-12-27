<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>MA</title>
<link rel="stylesheet" type="text/css" href="resources/css/ui-style.css" />
<script src="resources/js/jquery-1.9.1.min.js" type="text/javascript"></script>
<script type="text/javascript" src="resources/js/errorLog.js"></script>
</head>
<body>
	<t:header page="errorLog" title="jsp.errorLog.title" />

	<div class="content">
		<div class="singleBox">
			<input type="hidden" id="currentPage" value="${currentPage}" /> <input
				type="hidden" id="totalPageAmount" value="${totalPageAmount}" />
			<c:choose>
				<c:when test="${not empty log}">
					<div class="errorLogTitle">
						<fmt:message key="jsp.errorLog.importTitle" />
					</div>
					<div class="pagination"></div>
					<table class="errorLogTable">
						<thead>
							<th><fmt:message key="jsp.errorLog.table.date" /></th>
							<th><fmt:message key="jsp.errorLog.table.level" /></th>
							<th><fmt:message key="jsp.errorLog.table.logger" /></th>
							<th><fmt:message key="jsp.errorLog.table.message" /></th>
						</thead>
						<tbody>
							<c:forEach var="row" items="${log}">
								<tr>
									<td>${row.time}</td>
									<td>${row.level}</td>
									<td>${row.logger}</td>
									<td class="logMessage">${row.message}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<div class="pagination"></div>
				</c:when>
				<c:otherwise>
					<div class="errorLogTitle">
						<fmt:message key="jsp.errorLog.emptyLog" />
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</body>
</html>