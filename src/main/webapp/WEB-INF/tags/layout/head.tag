<%@ tag language="java" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="static" uri="/WEB-INF/tlds/static.tld" %>

<%
    String contextPath = request.getContextPath();
%>

<!doctype html>
	<html class="noscript">
			<head>
				<meta charset="utf-8">
				<meta name="google" content="notranslate" />
				<title>${Configurations.getString(schema, Constants.CONFIG_TITLE)}"</title>

				<link rel="shortcut icon" type="image/x-icon" href="<%= contextPath %>/static/images/favicon.ico" />
				<static:stylesheet fileName="biblivre.core.css" />
				<static:stylesheet fileName="biblivre.print.css" />
				<static:stylesheet fileName="jquery-ui.css" />
				<static:stylesheet fileName="font-awesome.min.css" />

				<static:script fileName="json.js" />
				<static:script fileName="jquery.js" />
				<static:script fileName="jquery-ui.js" />
				<static:script fileName="jquery.extras.js" />
				<static:script fileName="lodash.js" />

				<static:script fileName="globalize.js" />
				<static:script fileName="cultures/globalize.culture.<i18n:text key='language_code' />.js" />
				<script type="text/javascript" >Globalize.culture('<i18n:text key="language_code" />'); </script>
				<script type="text/javascript" >Globalize.culture().numberFormat.currency.symbol = '${Configurations.getString(schema, Constants.CONFIG_CURRENCY)}';</script>

				<static:script fileName="biblivre.core.js" />
				<static:script fileName="${requestScope.translationMap.cacheFileName}" />
	
				<c:set var="translateError" value="false" />
				<c:if test="${response.status eq 404}">
					<c:set var="message" value="error.file_not_found" />
					<c:set var="messageLevel" value="error" />
					<c:set var="messageLevel" value="true" />
				</c:if>
				<c:if test="${not empty message}">
					<script type="text/javascript">
					$(document).ready(function() {
						Core.msg({
							message: '" + message + "',
							message_level: '" + messageLevel + "',
							animate: true,
					<c:if test="${translateError}">
							translate: true,
					</c:if>
							sticky: true
						});
					});
					</script>
				</c:if>
			</head>
