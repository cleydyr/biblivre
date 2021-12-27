<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="menu" tagdir="/WEB-INF/tags/menu" %>

<li class="menu_help" data-module="help">
	<i18n:text key="menu.help" />
	<ul class="submenu">
		<menu:item module="help" action="help_about_biblivre" />
		<li class="submenu_help">
			<a href="http://www.biblivre.org.br/forum/viewforum.php?f=30" target="_blank">
				<i18n:text key="menu.help_faq" />
			</a>
		</li>
		<li class="submenu_help">
			<a href="static/Manual_Biblivre_5.0.0.pdf" target="_blank">
				<i18n:text key="menu.help_manual" />
			</a>
		</li>
	</ul>
</li>
<li>&#160;</li>
