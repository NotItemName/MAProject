<jsp:directive.tag language="java" pageEncoding="utf-8" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ attribute name="id" required="false" type="String"%>
<%@ attribute name="onchange" required="false" type="String"%>
<%@ attribute name="key" required="true" type="String"%>
<%@ attribute name="uncheck" required="false" type="Boolean"%>

<div class="checkboxBox">
	<input class="checkbox" type="checkbox" id="${id}"
		onchange="${onchange}" ${not unchecked ? 'checked="checked"' : ''} />
	<div class="name">
		<fmt:message key="${key}" />
	</div>
</div>