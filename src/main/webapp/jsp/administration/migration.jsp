<%@page import="biblivre.core.translations.TranslationsMap"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<script type="text/javascript">
		$(document).ready(function() {

		});

		var migrate = function(button) {

			var phaseGroups = [];
			$('input:checkbox:checked[name="phases"]').each(function() {
				phaseGroups.push($(this).val());
			});

			Core.clearFormErrors();

			$.ajax({
				url: window.location.pathname,
				type: 'POST',
				dataType: 'json',
				data: {
					controller: 'json',
					module: 'administration.datamigration',
					action: 'migrate',
					groups: phaseGroups
				},
				loadingButton: button,
				loadingTimedOverlay: true
			}).done($.proxy(function(response) {
				Core.msg(response);

				if (!response.success) {
					Core.formErrors(response.errors);
				}
			}, this));

		};
	</script>
</layout:head>

<layout:body>

</layout:body>
