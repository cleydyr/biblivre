<%@page import="biblivre.core.SchemaThreadLocal"%>
<%@page import="biblivre.core.utils.Constants"%>
<%@page import="biblivre.core.configurations.ConfigurationBO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="menu" tagdir="/WEB-INF/tags/menu" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="static" uri="/WEB-INF/tlds/static.tld" %>

<%
	ConfigurationBO configurationBO = (ConfigurationBO) request.getAttribute("configurations");
	String schema = SchemaThreadLocal.get();
	String contextPath = request.getContextPath();
%>

<!doctype html>
	<html class="noscript">
			<head>
				<meta charset="utf-8">
				<meta name="google" content="notranslate" />
				<title><%= configurationBO.getString(Constants.CONFIG_TITLE) %></title>

				<link rel="shortcut icon" type="image/x-icon" href="<%= contextPath %>/static/images/favicon.ico" />
				<static:stylesheet fileName="biblivre.core.css" />
				<static:stylesheet media="print" fileName="biblivre.print.css" />

				<static:script fileName="jquery.js" />
				<static:script fileName="jquery.extras.js" />
				<static:script fileName="lodash.js" />

				<static:script fileName="globalize.js" />
				<static:culture_script />

				<static:script fileName="biblivre.core.js" />
				<static:script fileName="${requestScope.translationsMap.getCacheFileName()}" />

				<c:set var="translateError" value="false" />
				<c:if test="${response.getStatus() == 404}">
					<c:set var="message" value="error.file_not_found" />
					<c:set var="messageLevel" value="error" />
					<c:set var="messageLevel" value="true" />
				</c:if>
				<c:if test="${not empty message}">
					<script type="text/javascript">
					$(document).ready(function() {
						Core.msg({
							message: '${message}',
							message_level: '${messageLevel}',
							animate: true,
					<c:if test="${translateError}">
							translate: true,
					</c:if>
							sticky: true
						});
					});
					</script>
				</c:if>
