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
<script src="resources/js/budgetCoins.js"></script>
</head>
<body>
	<t:header page="survey" title="jsp.prioritiesAssessment.title" />

	<div class="content">
		<div class="singleBox">
			<div class="surveyDescription">
				<t:surveyDescription surveyType="prioritization" />
			</div>
			<form action="" method="post">
				<div class="budgetBox">
					<div class="label">
						<fmt:message key="jsp.prioritiesAssessment.budget" />
					</div>
					<div class="value">
						<c:forEach var="index" begin="0" end="${questions.size() * 3}">
							<div class="emptyCoin">&nbsp;</div>
						</c:forEach>
					</div>
				</div>
				<t:questions questionType="prioritization" />
				<div class="rightAlign">
					<input class="button right" type="submit"
						value="<fmt:message key ='jsp.button.finish'/>" />
				</div>
			</form>
		</div>
	</div>
</body>
</html>