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
	<t:header page="survey" title="jsp.satisfactionAssessment.title" />

	<div class="content">
		<div class="singleBox">
			<div class="surveyDescription">
				<t:surveyDescription surveyType="satisfaction" />
			</div>
			<form action="" method="post">
				<t:questions questionType="satisfaction" />
				<div class="rightAlign">
					<input class="button right" type="submit"
						value="<fmt:message key ='jsp.button.continue'/>" />
				</div>
			</form>
		</div>
	</div>
</body>
</html>