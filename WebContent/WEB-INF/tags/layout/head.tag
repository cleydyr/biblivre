<%@ tag language="java" pageEncoding="UTF-8"%>

<%@ tag import="org.apache.commons.httpclient.HttpStatus"%>
<%@ tag import="biblivre.core.configurations.Configurations"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>

<!doctype html>
	<html class="noscript">
			<head>
				<meta charset="utf-8">
				<meta name="google" content="notranslate" />
				<title>${Configurations.getString(schema, Constants.CONFIG_TITLE)}"</title>

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
				<script type="text/javascript" >Globalize.culture().numberFormat.currency.symbol = '${Configurations.getString(schema, Constants.CONFIG_CURRENCY)}';</script>

				<script type="text/javascript" src="static/scripts/biblivre.core.js"></script>
				<script type="text/javascript" src="static/scripts/${requestScope.translationMap.cacheFileName}"></script>
	
				<c:set var="translateError" value="false" />
				<c:if test="${response.status eq HttpStatus.SC_NOT_FOUND}">
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
