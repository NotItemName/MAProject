<jsp:directive.tag language="java" pageEncoding="utf-8" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ attribute name="page" required="true" type="String"%>
<%@ attribute name="title" required="true" type="String"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>

<div class="header">
	<div class="logo"></div>
	<div class="title">
		<fmt:message key="${title}" />
	</div>
	<c:if test="${not empty user}">
		<div class="userInfo">
			<div class="avatar">
				<img src="resources/images/avatar.jpg">
			</div>
			<div class="name">${user.name}</div>
			<div class="logOut">
				<a href="logOut"><fmt:message key="jsp.header.logOut" /></a>
			</div>
		</div>
	</c:if>
	<t:localeChange />
</div>
<div class="tabs">
	<c:choose>
		<c:when
			test="${user.role.name() eq 'SUPERADMIN' or user.role.name() eq 'RM'}">
			<div class="tab">
				<a href="dashboard" ${page eq 'dashboard' ? 'class="selected"' : ''}><fmt:message
						key="jsp.header.dashboard" /></a>
			</div>

			<div class="tab">
				<a href="createSurvey"
					${page eq 'createSurvey' ? 'class="selected"' : ''}><fmt:message
						key="jsp.header.survey" /></a>
			</div>

			<c:if test="${user.role.name() eq 'SUPERADMIN'}">
				<div class="tab">
					<a href="errorLog" ${page eq 'errorLog' ? 'class="selected"' : ''}><fmt:message
							key="jsp.header.errorLog" /></a>
				</div>
				<div class="tab">
					<a href="updateDatabase"
						${page eq 'updateDatabase' ? 'class="selected"' : ''}><fmt:message
							key="jsp.header.updateDatabase" /></a>
				</div>
			</c:if>
		</c:when>
		<c:otherwise>
			&nbsp;
		</c:otherwise>
	</c:choose>
</div>