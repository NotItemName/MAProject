<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>MA</title>
<link rel="stylesheet" type="text/css" href="resources/css/ui-style.css" />
</head>
<body>
	<t:header page="updateDatabase" title="jsp.updateDatabase.title" />

	<div class="content">
		<div class="singleBox center">
			<div class="formContent">
				<form action="updateDatabase" enctype="multipart/form-data"
					method="post">
					<div class="infoMessage">
						<fmt:message key="jsp.updateDatabase.message.selectFile" />
					</div>					
					<input type="file" name="file" />
					<input  type="submit" value='<fmt:message key="jsp.updateDatabase.button.submitFile" />'/>
				</form>
			</div>
			<div class="errorMessage">
				<c:if test="${not empty message}">
					<fmt:message key="${message}" />
				</c:if>
			</div>
		</div>
	</div>

</body>
</html>