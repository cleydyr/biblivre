<#setting datetime_format="${dateTimeFormat}">
<#setting date_format="${dateFormat}">

<html>
	<head>
		<style type="text/css">
			table {
				border: 1px solid;
				padding: 10px;
				font-family: HelveticaNeue-Light, 'Helvetica Neue Light', 'Helvetica Neue', Helvetica, Arial, 'Lucida Grande', sans-serif;
				font-size: 14px;
				font-style: normal;
				font-variant: normal;
				font-weight: normal;
			}
			.header {
				text-align: center;
			}
			.left-cell {
				text-align: right;
			}
			.right-cell {
				text-align: left;
			}
		</style>
	</head>
	<body>
		<table>
			<tr><td colspan="2" class="header">${libraryName} - ${.now}</td></tr>
			<tr><td colspan="2"><hr /></td></tr>
			<#if (lendingInfo?size > 0)>
				<tr><td class="left-cell">${nameLabel}:</td><td class="right-cell">${userName}</td></tr>
				<tr><td class="left-cell">${idLabel}:</td><td class="right-cell">${enrollment}</td></tr>
				<tr><td colspan="2"><hr /></td></tr>
				<#if currentLendings?size != 0>
					<tr><td colspan="2" class="header">${header}</td></tr>
					<tr><td colspan="2"><hr /></td></tr>
					<tr>	<td>&nbsp;</td></tr>
					<#list currentLendings as item>
						<tr><td class="left-cell">${authorLabel}:</td><td class="right-cell">${item.biblio.author!}</td></tr>
						<tr><td class="left-cell">${titleLabel}:</td><td class="right-cell">${item.biblio.title}</td></tr>
						<tr><td class="left-cell">${biblioLabel}:</td><td class="right-cell">${item.holding.id}</td></tr>
						<tr><td class="left-cell">${holdingLabel}:</td><td class="right-cell">${item.holding.accessionNumber}</td></tr>
						<tr><td class="left-cell">${lendingDateLabel}:</td><td class="right-cell">${item.lending.created}</td></tr>
						<tr><td class="left-cell">${expectedDateLabel}:</td><td class="right-cell">${item.lending.expectedReturnDate?date}</td></tr>
						<tr><td>&nbsp;</td></tr>
					</#list>
					<tr><td colspan="2"><hr /></td></tr>
				</#if>
				<#if currentRenews?size != 0>
					<tr><td colspan="2" class="header">${renewsHeader}</td></tr>
					<tr><td colspan="2"><hr /></td></tr>
					<tr>	<td>&nbsp;</td></tr>
					<#list currentRenews as item>
						<tr><td class="left-cell">${authorLabel}:</td><td class="right-cell">${item.biblio.author!}</td></tr>
						<tr><td class="left-cell">${titleLabel}:</td><td class="right-cell">${item.biblio.title}</td></tr>
						<tr><td class="left-cell">${biblioLabel}:</td><td class="right-cell">${item.holding.id}</td></tr>
						<tr><td class="left-cell">${holdingLabel}:</td><td class="right-cell">${item.holding.accessionNumber}</td></tr>
						<tr><td class="left-cell">${lendingDateLabel}:</td><td class="right-cell">${item.lending.created}</td></tr>
						<tr><td class="left-cell">${expectedDateLabel}:</td><td class="right-cell">${item.lending.expectedReturnDate?date}</td></tr>
						<tr><td>&nbsp;</td></tr>
					</#list>
					<tr><td colspan="2"><hr /></td></tr>
				</#if>
				<#if currentReturns?size != 0>
					<tr><td colspan="2" class="header">${returnsHeader}</td></tr>
					<tr><td colspan="2"><hr /></td></tr>
					<tr>	<td>&nbsp;</td></tr>
					<#list currentReturns as item>
						<tr><td class="left-cell">${authorLabel}:</td><td class="right-cell">${item.biblio.author!}</td></tr>
						<tr><td class="left-cell">${titleLabel}:</td><td class="right-cell">${item.biblio.title}</td></tr>
						<tr><td class="left-cell">${biblioLabel}:</td><td class="right-cell">${item.holding.id}</td></tr>
						<tr><td class="left-cell">${holdingLabel}:</td><td class="right-cell">${item.holding.accessionNumber}</td></tr>
						<tr><td class="left-cell">${lendingDateLabel}:</td><td class="right-cell">${item.lending.created}</td></tr>
						<tr><td class="left-cell">${returnDateLabel}:</td><td class="right-cell">${item.lending.expectedReturnDate?date}</td></tr>
						<tr><td>&nbsp;</td></tr>
					</#list>
					<tr><td colspan="2"><hr /></td></tr>
				</#if>
			</#if>
		</table>
	</body>
</html>	
	