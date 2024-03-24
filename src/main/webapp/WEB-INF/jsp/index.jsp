<%@page import="java.util.Collection"%>
<%@page import="biblivre.core.utils.Constants"%>
<%@page import="biblivre.core.configurations.ConfigurationBO"%>
<%@page import="biblivre.core.schemas.SchemaDTO"%>
<%@page import="biblivre.core.ExtendedRequest"%>
<%@page import="biblivre.core.SchemaThreadLocal"%>
<%@ page import="biblivre.login.LoginDTO"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
ConfigurationBO configurationBO = (ConfigurationBO) request.getAttribute("configurations");
%>

<layout:head>
	<link rel="stylesheet" type="text/css" href="/static/styles/biblivre.index.css" />
	<link rel="stylesheet" type="text/css" href="/static/styles/biblivre.multi_schema.css" />
</layout:head>

<layout:body>
	<%
	ExtendedRequest req = (ExtendedRequest) request;

	if (!SchemaThreadLocal.isGlobalSchema()) {
	%>
		<div class="text">
			<%
				LoginDTO login = (LoginDTO) session.getAttribute(request.getAttribute("schema") + ".logged_user");
				pageContext.setAttribute("login", login);

				if (login != null) {
					pageContext.setAttribute("name", login.getFirstName());
				}
			%>

			<c:choose>
				<c:when test="${empty login}">
					<i18n:text key="text.main.logged_out" />
				</c:when>
				<c:otherwise>
					<i18n:text key="text.main.logged_in" param1="${name}" />
				</c:otherwise>
			</c:choose>
		</div>
	<%
	} else {
	%>
		<div class="multischema biblivre_form">
			<fieldset>
				<legend><i18n:text key="text.multi_schema.select_library" /></legend>
				<%
					for (final SchemaDTO schema : (Collection<SchemaDTO>) request.getAttribute("schemas")) {
						if (schema.isDisabled()) {
							continue;
						}

						String currentSchema = SchemaThreadLocal.remove();

				        SchemaThreadLocal.setSchema(schema.getSchema());
				%>
							<div class="library">
								<a href="<%= schema.getSchema() %>/"><%= configurationBO.getHtml(Constants.CONFIG_TITLE) %></a>
								<div class="subtitle"><%= configurationBO.getHtml(Constants.CONFIG_SUBTITLE) %></div>
							</div>
				<%
						SchemaThreadLocal.remove();

		        		SchemaThreadLocal.setSchema(currentSchema);
					}
				%>
			</fieldset>
		</div>
	<%
	}
	%>
</layout:body>
