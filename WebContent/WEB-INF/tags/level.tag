<%@ tag language="java" pageEncoding="UTF-8"%>

<%@ attribute name="module" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>

<%@ tag import="biblivre.core.auth.AuthorizationPoints"%>

<li class="menu_${module}" data-module="${module}">
	<i18n:text key="menu.${module}" />
	<ul class="submenu" >
		<jsp:doBody />
	</ul>
</li>