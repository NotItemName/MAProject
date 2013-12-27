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
	<t:header page="error"
		title="jsp.home.title" />

	<div class="content">
		<div class="singleBox">
			<div class="label">
				<fmt:message key="jsp.home.title" />
			</div>
			<div class="homeText">
				<c:choose>
					<c:when
						test="${user.role.name() eq 'SUPERADMIN' or user.role.name() eq 'RM'}">
						<fmt:message key="jsp.home.rmText" />
					</c:when>
					<c:otherwise>
						<fmt:message key="jsp.home.employeeText" />
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</body>
</html>