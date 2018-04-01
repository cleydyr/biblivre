<%@ tag language="java" pageEncoding="UTF-8"%>

<%@ attribute name="action" required="true" %>
<%@ attribute name="module" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>

<%@ tag import="biblivre.core.auth.AuthorizationPoints"%>

<c:set var="atpsAttr" value="${requestScope.schema}.logged_user_atps" />
<c:set var="atps" value="${sessionScope[atpsAttr]}" />
<c:if test="${empty atps}">
	<c:set var="atps" value="${requestScope.notLoggedAtps}" />
</c:if>
<c:choose>
<c:when test="${empty atps or atps.isAllowed('menu', action)}">
	<li class="submenu_${module}" data-action="${action}">
		<a href="?action=${action}"><i18n:text key="menu.${action}" /></a>
	</li>
</c:when>
<c:otherwise>
	<li class="disabled" data-action="${action}">
		<i18n:text key="menu${action}" />
	</li>
</c:otherwise>
</c:choose>
