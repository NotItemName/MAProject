<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>MA</title>
<link rel="stylesheet" type="text/css" href="resources/css/ui-style.css" />
</head>
<body>
	<t:header page="error" title="jsp.accessDenied.message" />

	<div class="content">
		<div class="singleBox">
			<div class="label">
				<fmt:message key="jsp.accessDenied.message" />
			</div>
		</div>
	</div>
</body>
</html>