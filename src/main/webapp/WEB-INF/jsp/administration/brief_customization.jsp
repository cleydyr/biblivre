<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="static" uri="/WEB-INF/tlds/static.tld" %>

<layout:head>
	<static:stylesheet fileName="biblivre.administration.customization.css" />
	<static:stylesheet fileName="font-awesome.min.css" />
</layout:head>

<layout:body multiPart="true">

	<div class="page_help"><i18n:text key="administration.brief_customization.page_help" /></div>
	<div id="brief_customization">
		<fieldset class="block">
			<legend><i18n:text key="administration.brief_customization.select_record_type" /></legend>

			<div class="record_type_selection">
				<select id="record_type_select" name="record_type_field" class="combo combo_auto_size">
					<option value="bibliographic"><i18n:text key="administration.brief_customization.biblio" /></option>
					<option value="authorities"><i18n:text key="administration.brief_customization.authorities" /></option>
					<option value="vocabulary"><i18n:text key="administration.brief_customization.vocabulary" /></option>
				</select>
			</div>
		</fieldset>

		<div id="datafields"></div>
		<textarea id="datafields_template" class="template"><!--
			{#foreach $T.datafields as datafield}
				<fieldset class="block" data-datafield="{ $T.datafield.datafieldTag }">
					<legend>{$T.datafield.datafieldTag} - {Translations.get('marc.bibliographic.datafield.' + $T.datafield.datafieldTag) }</legend>
					<div class="buttons">
						<span class="cancel-datafield"><i class="fa fa-close"></i></span>
						<span class="save-datafield"><i class="fa fa-check"></i></span>
						<span class="disable-datafield"><i class="fa fa-ban"></i></span>
						<span class="edit-datafield"><i class="fa fa-pencil"></i></span>
						<span class="move-datafield"><i class="fa fa-bars"></i></span>
					</div>
					<div class="edit_area"></div>
				</fieldset>
			{#/for}
		--></textarea>
		<textarea id="datafields_edit_template" class="template"><!--
			<div class="format-input">{ $T.format }</div>
			<div class="subtitle"><i18n:text key="administration.brief_customization.subfields_title" /></div>
			<div class="subfields">
				{#foreach $T.subfields as subfield}
					<div class="subfield">{ '$' + $T.subfield.subfield }<span class="text"> - {Translations.get('marc.bibliographic.datafield.' + $T.datafield + '.subfield.' + $T.subfield.subfield) }<span></div>
				{#/for}
				<div class="clear"></div>
			</div>
			<div class="subtitle"><i18n:text key="administration.brief_customization.separators_title" /></div>
			<div class="separators">
				{#foreach $T.separators as separator}
					<div class="separator">{ $T.separator.separator.replace(/ /g, '&nbsp;') }<span class="text">&nbsp;&nbsp;- {Translations.get('administration.brief_customization.separators.' + $T.separator.code) }<span></div>
				{#/for}
				<div class="clear"></div>
			</div>
			<div class="subtitle"><i18n:text key="administration.brief_customization.aggregators_title" /></div>
			<div class="aggregators">
				{#foreach $T.aggregators as aggregator}
					<div class="aggregator">{ $T.aggregator.aggregator.replace(/ /g, '&nbsp;') }<span class="text">&nbsp;&nbsp;&nbsp;- {Translations.get('administration.brief_customization.aggregators.' + $T.aggregator.code) }<span></div>
				{#/for}
				<div class="clear"></div>
			</div>
		--></textarea>

		<br />
		<h1><i18n:text key="administration.brief_customization.available_fields.description" /></h1>
		<div class="spacer"></div>

		<div id="disabled_datafields"></div>
		<textarea id="disabled_datafields_template" class="template"><!--
			{#foreach $T.disabled_datafields as datafield}
				<div class="block" data-datafield="{ $T.datafield.datafield }">
					<div class="data-name">{$T.datafield.datafield} - {Translations.get('marc.bibliographic.datafield.' + $T.datafield.datafield) }</div>
					<div class="buttons">
						<span class="add-datafield"><i class="fa fa-plus"></i></span>
					</div>
				</div>
			{#/for}
		--></textarea>
	</div>
	<static:script fileName="jquery-ui.js" />
	<static:script fileName="biblivre.administration.brief_customization.js" />
	<static:script fileName="${requestScope.biblioCacheFileName}" />
	<script>Customization.formFields['bibliographic'] = _.keyBy(CatalogingInput.formFields, 'datafield');</script>
	<static:script fileName="${requestScope.authoritiesCacheFileName}" />
	<script>Customization.formFields['authorities'] = _.keyBy(CatalogingInput.formFields, 'datafield');</script>
	<static:script fileName="${requestScope.vocabulariesCacheFileName}" />
	<script>Customization.formFields['vocabulary'] = _.keyBy(CatalogingInput.formFields, 'datafield');</script>
</layout:body>
