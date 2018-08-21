<#setting datetime_format="${dateTimeFormat}">

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
		</style>
	</head>
	<body>
		<table>
			<tr><td colspan="2" style="text-align: center;">${libraryName} - ${.now}</td></tr>
			<tr><td colspan="2"><hr /></td></tr>
			<#if (lendingInfo?size > 0)>
				<tr><td style="width: 40%; text-align: right;">${nameLabel}:</td><td style="text-align: left">${userName}</td></tr>
				<tr><td style="width: 40%; text-align: right;">${idLabel}:</td><td style="text-align: left">${enrollment}</td></tr>
				<tr><td colspan="2"><hr /></td></tr>
				<#if currentLendings?size != 0>
					<tr><td colspan="2" style="text-align: center;">${header}</td></tr>
					<tr><td colspan="2"><hr /></td></tr>
					<tr>	<td>&nbsp;</td></tr>
					<#list currentLendings as item>
						<tr><td style="width: 40%; text-align: right;">${authorLabel}:</td><td style="text-align: left">${item.biblio.author}</td></tr>
						<tr><td style="width: 40%; text-align: right;">${titleLabel}:</td><td style="text-align: left">${item.biblio.title}</td></tr>
						<tr><td style="width: 40%; text-align: right;">${biblioLabel}:</td><td style="text-align: left">${item.holding.id}</td></tr>
						<tr><td style="width: 40%; text-align: right;">${holdingLabel}:</td><td style="text-align: left">${item.holding.accessionNumber}</td></tr>
						<tr><td style="width: 40%; text-align: right;">${lendingDateLabel}:</td><td style="text-align: left">${item.lending.created}</td></tr>
						<tr><td style="width: 40%; text-align: right;">${exptectedDateLabel}:</td><td style="text-align: left">${item.lending.expectedReturnDate}</td></tr>
						<tr><td>&nbsp;</td></tr>
					</#list>
					<tr><td colspan="2"><hr /></td></tr>
				</#if>
				<#if currentRenews?size != 0>
					<tr><td colspan="2" style="text-align: center;">${renewalsHeader}</td></tr>
					<tr><td colspan="2"><hr /></td></tr>
					<tr>	<td>&nbsp;</td></tr>
					<#list currentRenews as item>
						<tr><td style="width: 40%; text-align: right;">${authorLabel}:</td><td style="text-align: left">${item.biblio.author}</td></tr>
						<tr><td style="width: 40%; text-align: right;">${titleLabel}:</td><td style="text-align: left">${item.biblio.title}</td></tr>
						<tr><td style="width: 40%; text-align: right;">${biblioLabel}:</td><td style="text-align: left">${item.holding.id}</td></tr>
						<tr><td style="width: 40%; text-align: right;">${holdingLabel}:</td><td style="text-align: left">${item.holding.accessionNumber}</td></tr>
						<tr><td style="width: 40%; text-align: right;">${lendingDateLabel}:</td><td style="text-align: left">${item.lending.created}</td></tr>
						<tr><td style="width: 40%; text-align: right;">${exptectedDateLabel}:</td><td style="text-align: left">${item.lending.expectedReturnDate}</td></tr>
						<tr><td>&nbsp;</td></tr>
					</#list>
					<tr><td colspan="2"><hr /></td></tr>
				</#if>
				<#if currentRenews?size != 0>
					<tr><td colspan="2" style="text-align: center;">${renewsHeader}</td></tr>
					<tr><td colspan="2"><hr /></td></tr>
					<tr>	<td>&nbsp;</td></tr>
					<#list currentRenews as item>
						<tr><td style="width: 40%; text-align: right;">${authorLabel}:</td><td style="text-align: left">${item.biblio.author}</td></tr>
						<tr><td style="width: 40%; text-align: right;">${titleLabel}:</td><td style="text-align: left">${item.biblio.title}</td></tr>
						<tr><td style="width: 40%; text-align: right;">${biblioLabel}:</td><td style="text-align: left">${item.holding.id}</td></tr>
						<tr><td style="width: 40%; text-align: right;">${holdingLabel}:</td><td style="text-align: left">${item.holding.accessionNumber}</td></tr>
						<tr><td style="width: 40%; text-align: right;">${lendingDateLabel}:</td><td style="text-align: left">${item.lending.created}</td></tr>
						<tr><td style="width: 40%; text-align: right;">${returnDateLabel}:</td><td style="text-align: left">${item.lending.expectedReturnDate}</td></tr>
						<tr><td>&nbsp;</td></tr>
					</#list>
					<tr><td colspan="2"><hr /></td></tr>
				</#if>
			</#if>
		</table>
	</body>
</html>	
	