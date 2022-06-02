<%@page import="biblivre.administration.accesscards.AccessCardStatus"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="resources/styles/biblivre.search.css" />

	<script type="text/javascript" src="resources/scripts/biblivre.search.js"></script>
	<script type="text/javascript" src="resources/scripts/biblivre.administration.accesscards.search.js"></script>
	<script type="text/javascript" src="resources/scripts/biblivre.input.js"></script>
	<script type="text/javascript" src="resources/scripts/biblivre.administration.accesscards.input.js"></script>
	<script type="text/javascript">
		var AccessCardsSearch = CreateSearch(AccessCardsSearchClass, {
			type: 'administration.accesscards',
			root: '#access_cards',
			autoSelect: false,
			autoSearch: true,
			enableTabs: false,
			enableHistory: false
		});

		AccessCardsInput.type = 'administration.accesscards';
		AccessCardsInput.root = '#access_cards';
		AccessCardsInput.search = AccessCardsSearch;
	</script>
	
</layout:head>

<layout:body>
	<div id="root"></div>
</layout:body>

