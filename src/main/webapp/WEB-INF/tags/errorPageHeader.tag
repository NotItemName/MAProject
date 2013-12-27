<jsp:directive.tag language="java" pageEncoding="utf-8" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ attribute name="title" required="true" type="String"%>

<div class="header">
	<div class="logo"></div>
	<div class="title">
		<fmt:message key="${title}" />
	</div>
</div>
<div class="tabs">&nbsp;</div>