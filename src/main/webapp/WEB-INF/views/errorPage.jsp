<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>MA</title>
<link rel="stylesheet" type="text/css" href="resources/css/ui-style.css" />
</head>
<body>
	<t:errorPageHeader title="jsp.errorPage.message" />

	<div class="content">
		<div class="singleBox">
			<c:set var="code"
				value="${requestScope['javax.servlet.error.status_code']}" />
			<div class="errorMessage">
				<fmt:message key="jsp.errorPage.info" />
				<c:if test="${not empty code}">
					<div>
						<fmt:message key="jsp.errorPage.errorCode" />
						<c:out value="${code}" />
					</div>
				</c:if>
			</div>
		</div>
	</div>
</body>
</html>