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

	<t:header page="error" title="jsp.logIn.title" />
	<div class="content">
		<div class="singleBox">
			<div class="loginFormBox">
				<form action="auth" method="post">
					<div class="label">
						<fmt:message key="jsp.logIn.userId" />
					</div>
					<div class="inputBox">
						<input class="input" type="text" name="login"></input>
					</div>
					<div class="label">
						<fmt:message key="jsp.logIn.password" />
					</div>
					<div class="inputBox">
						<input class="input" type="password" name="password"></input>
					</div>
					<input class="button" type="submit"
						value="<fmt:message key='jsp.button.logIn'/> "></input>
					<c:if test="${not empty sessionScope.loginErrorMessage}">
						<div class="errorMessage">
							<fmt:message key='${sessionScope.loginErrorMessage}' />
						</div>
					</c:if>
				</form>
			</div>
		</div>
	</div>
</body>
</html>