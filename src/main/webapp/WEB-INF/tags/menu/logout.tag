<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="menu" tagdir="/WEB-INF/tags/menu" %>

<li data-module="logout" class="logout">
	<button onclick="Core.submitForm('login', 'logout', 'jsp');" type="button">
		<i18n:text key="label.logout" />
	</button>
</li>