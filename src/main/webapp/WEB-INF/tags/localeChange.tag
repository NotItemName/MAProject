<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script>
	function selectLang(event) {
		var src = event.target ? event.target : event.srcElement;
		if (src.tagName == "SPAN") {
			var lang = src.getAttribute("id");
			var url = window.location;

			if (url.search.length == 0) {
				url.search = "locale=" + lang;
			} else if (url.search.indexOf("locale") == -1)
				url.search = url.search + "&locale=" + lang;
			else {
				var newLocale = "locale=" + lang;
				url.search = url.search.replace(/locale=\w+/, newLocale);
			}
		}
	}
</script>

<div id="localeChange" onClick="selectLang(event)">
	<c:forEach var="loc" items="${supportedLocales}">
		<span id="${loc.language}" class="locale_ref">${loc.language.toUpperCase()}</span>
	</c:forEach>
</div>
