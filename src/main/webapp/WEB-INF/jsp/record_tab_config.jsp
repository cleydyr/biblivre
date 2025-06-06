<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="static" uri="/WEB-INF/tlds/static.tld" %>

<layout:head>
	<static:stylesheet fileName="admin.css" />
    <static:script fileName="jquery.ui.js" />
    <static:script fileName="admin.js" />
</layout:head>

<layout:body>

<div class="datafield_list">
	<div class="datafield_entry sortable">
		<div class="datafield_info">
			<div class="datafield">Datafield 100</div>
			<span class="label">Autor</span>:
			<span class="format">SubcampoA SubcampoB: SubcampoC SubcampoA SubcampoB: SubcampoC SubcampoA SubcampoB: SubcampoC SubcampoA SubcampoB: SubcampoC</span>
		</div>
		<div class="clear"></div>
		<div class="datafield_edit">
			<div class="form_label">Datafield</div>
			<div class="form_field"><input type="text"></div>
			<div class="form_label">Label</div>
			<div class="form_field"><input type="text"></div>
			<div class="form_label">Format</div>
			<div class="form_field">
				<div class="button_plus"></div>
				<div class="datafield_formats"></div>
			</div>
		</div>
	</div>
	<div class="datafield_entry sortable">
		<div class="datafield_info">
			<div class="datafield">Datafield 100</div>
			<span class="label">Autor</span>:
			<span class="format">SubcampoA SubcampoB: SubcampoC SubcampoA SubcampoB: SubcampoC SubcampoA SubcampoB: SubcampoC SubcampoA SubcampoB: SubcampoC</span>
		</div>
		<div class="clear"></div>
		<div class="datafield_edit">
			<div class="form_label">Datafield</div>
			<div class="form_field"><input type="text"></div>
			<div class="form_label">Label</div>
			<div class="form_field"><input type="text"></div>
			<div class="form_label">Format</div>
			<div class="form_field">
				<div class="button_plus"></div>
				<div class="datafield_formats"></div>
			</div>
		</div>
	</div>
	<div class="datafield_entry sortable">
		<div class="datafield_info">
			<div class="datafield">Datafield 100</div>
			<span class="label">Autor</span>:
			<span class="format">SubcampoA SubcampoB: SubcampoC SubcampoA SubcampoB: SubcampoC SubcampoA SubcampoB: SubcampoC SubcampoA SubcampoB: SubcampoC</span>
		</div>
		<div class="clear"></div>
		<div class="datafield_edit">
			<div class="form_label">Datafield</div>
			<div class="form_field"><input type="text"></div>
			<div class="form_label">Label</div>
			<div class="form_field"><input type="text"></div>
			<div class="form_label">Format</div>
			<div class="form_field">
				<div class="button_plus"></div>
				<div class="datafield_formats"></div>
			</div>
		</div>
	</div>
</div>
<div class="clear"></div>

</layout:body>
