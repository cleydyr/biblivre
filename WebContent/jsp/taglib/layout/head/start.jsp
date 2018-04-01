<%@page import="biblivre.core.utils.Constants"%>
<%@page import="biblivre.core.configurations.Configurations"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="menu" tagdir="/WEB-INF/tags/menu" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>

<jsp:useBean id="schema" type="java.lang.String" scope="request" />

<!doctype html>
	<html class="noscript">
			<head>
				<meta charset="utf-8">
				<meta name="google" content="notranslate" />
				<title><%= Configurations.getString(schema, Constants.CONFIG_TITLE) %></title>

				<link rel="shortcut icon" type="image/x-icon" href="static/images/favicon.ico" />
				<link rel="stylesheet" type="text/css" href="static/styles/biblivre.core.css" />
				<link rel="stylesheet" type="text/css" href="static/styles/biblivre.print.css" />
				<link rel="stylesheet" type="text/css" href="static/styles/jquery-ui.css" />
				<link rel="stylesheet" type="text/css" href="static/styles/font-awesome.min.css" />

				<script type="text/javascript" src="static/scripts/json.js"></script>
				<script type="text/javascript" src="static/scripts/jquery.js"></script>
				<script type="text/javascript" src="static/scripts/jquery-ui.js"></script>
				<script type="text/javascript" src="static/scripts/jquery.extras.js"></script>
				<script type="text/javascript" src="static/scripts/lodash.js"></script>

				<script type="text/javascript" src="static/scripts/globalize.js"></script>
				<script type="text/javascript" src="static/scripts/cultures/globalize.culture.<i18n:text key='language_code' />.js"></script>
				<script type="text/javascript" >Globalize.culture('<i18n:text key="language_code" />'); </script>
				<script type="text/javascript" >Globalize.culture().numberFormat.currency.symbol = '<%= Configurations.getString(schema, Constants.CONFIG_CURRENCY) %>';</script>

				<script type="text/javascript" src="static/scripts/biblivre.core.js"></script>
				<script type="text/javascript" src="static/scripts/${requestScope.translationsMap.getCacheFileName()}"></script>
	
				<c:set var="translateError" value="false" />
				<c:if test="${pageContext.getResponse().getStatus() == 404}">
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