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
				<script type="text/javascript" src="static/scripts/globalize.js"></script>
				<script type="text/javascript" src="static/scripts/cultures/globalize.culture.<i18n:text key='language_code' />.js"></script>
				<script type="text/javascript" >Globalize.culture('<i18n:text key="language_code" />'); </script>
				<script type="text/javascript" >Globalize.culture().numberFormat.currency.symbol = '${Configurations.getString(schema, Constants.CONFIG_CURRENCY)}';</script>

				<script type="text/javascript" src="static/scripts/${requestScope.translationMap.cacheFileName}"></script>
	
				<c:set var="translateError" value="false" />
				<c:if test="${response.status eq HttpStatus.SC_NOT_FOUND}">
					<c:set var="message" value="error.file_not_found" />
					<c:set var="messageLevel" value="error" />
					<c:set var="messageLevel" value="true" />
				</c:if>
				<c:if test="${not empty message}">
					<script type="text/javascript">
					
					</script>
				</c:if>
			</head>
