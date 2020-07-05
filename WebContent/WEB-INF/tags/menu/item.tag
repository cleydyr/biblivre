<%@ tag language="java" pageEncoding="UTF-8"%>

<%@ attribute name="action" required="true" %>
<%@ attribute name="module" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>

<%@ tag import="biblivre.core.auth.AuthorizationPoints"%>

<li class="submenu_${module}" data-action="${action}">
	<a href="?action=${action}"><i18n:text key="menu.${action}" /></a>
</li>
