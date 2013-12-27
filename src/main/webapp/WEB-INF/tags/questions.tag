<jsp:directive.tag language="java" pageEncoding="utf-8" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ attribute name="questionType" required="true" type="String"%>

<c:set var="questionIndex" value="0" />
<c:forEach var="question" items="${questions}">
	<c:set var="questionIndex" value="${questionIndex + 1}" />
	<div class="questionBox">
		<c:forEach var="questionText" items="${question.questionText}">
			<c:if
				test="${questionText.key eq pageContext.request.locale.language}">
				<c:choose>
					<c:when test="${questionType eq 'satisfaction'}">
						<div class="number">${questionIndex}</div>
						<div class="name">${questionText.value.satisfactionText}:</div>
						<div class="radioBox">
							<c:forEach var="index" begin="0" end="6">
								<div class="radio">
									<input type="radio" name="satisfaction${question.id}"
										value="${index}" ${index eq 3 ? 'checked="checked"' : ''}>
									<div class="value">${index}</div>
								</div>
							</c:forEach>
						</div>
					</c:when>
					<c:otherwise>
						<div class="number">${questionIndex}</div>
						<div class="name">${questionText.value.prioritizationText}:</div>
						<div class="moneyBox">
							<input class="moneyButton button minus" type="button" value="-">
							<div class="value">
								<c:forEach var="index" begin="0" end="5">
									<c:if test="${index < 3}">
										<div class="coin">O</div>
									</c:if>
									<c:if test="${index >= 3}">
										<div class="emptyCoin">&nbsp;</div>
									</c:if>
								</c:forEach>
							</div>
							<input class="moneyButton button plus" type="button" value="+">
							<input type="hidden" name="priority${question.id}" value="3" />
						</div>
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:forEach>
		<div class="clear"></div>
	</div>
</c:forEach>