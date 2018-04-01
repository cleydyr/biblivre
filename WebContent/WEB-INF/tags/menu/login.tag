<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="menu" tagdir="/WEB-INF/tags/menu" %>

<li data-module="login" class="login">
	<button onclick="Core.submitForm('login', 'login', 'jsp');">
		<i18n:text key="label.login" />
	</button>
</li>
<li class="inputs">&#160;
	<input type="text" name="username" placeholder="<i18n:text key='label.username' />" />
	<input type="password" name="password" placeholder="<i18n:text key='label.password' />" />
</li>
