<%@page import="biblivre.core.utils.Constants"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="static" uri="/WEB-INF/tlds/static.tld" %>

<layout:head>
	<static:stylesheet fileName="biblivre.search.css" />
	<static:stylesheet fileName="biblivre.cataloging.import.css" />

	<static:script fileName="biblivre.cataloging.import.js" />
</layout:head>

<layout:body multiPart="true">
	<div class="page_help"><i18n:text key="cataloging.import.page_help" /></div>

	<div class="page_title">
		<div class="search_icon"></div>

		<div class="text">
			<i18n:text key="cataloging.import.title" /> - <span class="step"><i18n:text key="common.step" /> 1</span>
			<div class="subtext"><i18n:text key="cataloging.import.step_1_title" /></div>
		</div>
	</div>
	<div class="clear"></div>

	<fieldset class="wizard" id="step_1">
		<div class="step_description"><i18n:text key="cataloging.import.step_1_description" /></div>

		<div class="selection_box">
			<div class="title"><input type="radio" name="source" id="source_file" checked="checked"/> <label for="source_file"><i18n:text key="cataloging.import.source_file_title" /></label></div>
			<div class="subtitle"><i18n:text key="cataloging.import.source_file_subtitle" /></div>
			<div class="body">
				<input type="file" class="import_file" name="file"/>

				<div class="search_button_div">
					<a class="button arrow_right main_button" onclick="Import.upload(this);"><i18n:text key="cataloging.import.upload_button" /></a>
				</div>
			</div>
		</div>
	</fieldset>

	<fieldset class="wizard" id="step_2">
		<div class="step_description"><i18n:text key="cataloging.import.step_2_description" /></div>

		<div class="paging_bar"></div>

		<div class="search_results_box">
			<div id="search_results" class="search_results"></div>

			<textarea class="search_result_template template"><!--
				<div class="result {#if ($T.data.index + 1) % 2}odd{#else}even{#/if} {#if $T.data.overlay}{$T.data.overlay}{#/if}" data-index="{$T.data.index}">
					<div class="result_overlay"><div class="text">{$T.data.overlay_text}</div></div>
					<div class="buttons">
						<strong><i18n:text key="cataloging.import.import_as" /></strong><br/>
						<input type="radio" name="record_type_{$T.data.index}" id="rt_{$T.data.index}_i" value="ignore" /> <label for="rt_{$T.data.index}_i"><i18n:text key="cataloging.import.type.do_not_import" /></label><br/>
						<input type="radio" name="record_type_{$T.data.index}" id="rt_{$T.data.index}_b" value="biblio" /> <label for="rt_{$T.data.index}_b"><i18n:text key="cataloging.import.type.biblio" /></label><br/>
						<input type="radio" name="record_type_{$T.data.index}" id="rt_{$T.data.index}_a" value="authorities" /> <label for="rt_{$T.data.index}_a"><i18n:text key="cataloging.import.type.authorities" /></label><br/>
						<input type="radio" name="record_type_{$T.data.index}" id="rt_{$T.data.index}_v" value="vocabulary" /> <label for="rt_{$T.data.index}_v"><i18n:text key="cataloging.import.type.vocabulary" /></label><br/>
					</div>
					<div class="record">
						{#if $T.record.title}<label><i18n:text key="search.bibliographic.title" /></label>: {$T.record.title}<br/>{#/if}
						{#if $T.record.author}<label><i18n:text key="search.bibliographic.author" /></label>: {$T.record.author}<br/>{#/if}
						{#if $T.record.publication_year}<label><i18n:text key="search.bibliographic.publication_year" /></label>: {$T.record.publication_year}<br/>{#/if}
						{#if $T.record.shelf_location}<label><i18n:text key="search.bibliographic.shelf_location" /></label>: {$T.record.shelf_location}<br/>{#/if}
						{#if $T.record.isbn}<label><i18n:text key="search.bibliographic.isbn" /></label>: {$T.record.isbn}<br/>{#/if}
						{#if $T.record.issn}<label><i18n:text key="search.bibliographic.issn" /></label>: {$T.record.issn}<br/>{#/if}
						{#if $T.record.isrc}<label><i18n:text key="search.bibliographic.isrc" /></label>: {$T.record.isrc}<br/>{#/if}
						<div class="right">
							<a class="button" onclick="Import.marcEdit({$T.data.index});"><i18n:text key="cataloging.import.button.edit_marc" /></a>
							<a class="button" onclick="Import.import({$T.data.index}, {$T.data.index});"><i18n:text key="cataloging.import.button.import_this_record" /></a>
						</div>
					</div>
					<div class="clear"></div>
				</div>
			--></textarea>
		</div>

		<div class="footer_buttons">
			<a class="main_button center" onclick="Import.importCurrentPage();"><i18n:text key="cataloging.import.button.import_this_page" /></a>
			<a class="main_button center" onclick="Import.importAll();"><i18n:text key="cataloging.import.button.import_all" /></a>
		</div>


		<div class="paging_bar"></div>

	</fieldset>

	<div id="upload_popup" class="popup">
		<fieldset class="upload">
			<legend><i18n:text key="cataloging.import.upload_popup.title" /></legend>

			<div class="description">
				<p class="uploading"><i18n:text key="cataloging.import.upload_popup.uploading" /></p>
				<p class="processing"><i18n:text key="cataloging.import.upload_popup.processing" /></p>
			</div>

			<div class="progress">
				<div class="progress_text"><i18n:text key="common.wait" /></div>
				<div class="progress_bar">
					<div class="progress_bar_outer"><div class="progress_bar_inner"></div></div>
				</div>
			</div>
		</fieldset>
	</div>

	<div id="import_popup" class="popup">
		<fieldset class="import">
			<legend><i18n:text key="cataloging.import.import_popup.title" /></legend>

			<div class="description">
				<p class="importing"><i18n:text key="cataloging.import.import_popup.importing" /></p>
			</div>

			<div class="progress">
				<div class="progress_text"><i18n:text key="common.wait" /></div>
				<div class="progress_bar">
					<div class="progress_bar_outer"><div class="progress_bar_inner"></div></div>
				</div>
			</div>
		</fieldset>
	</div>

	<div id="marc_popup" class="popup">
		<fieldset class="upload">
			<legend><i18n:text key="cataloging.import.marc_popup.title" /></legend>

			<div class="description">
				<p><i18n:text key="cataloging.import.marc_popup.description" /></p>
			</div>

			<div class="marc">
				<textarea id="marc_popup_textarea"></textarea>
			</div>

			<div class="buttons">
				<a class="button" onclick="Import.hideMarcEdit();"><i18n:text key="common.cancel" /></a>
				<a class="button main_button" onclick="Import.marcChange();"><i18n:text key="common.ok" /></a>
			</div>
		</fieldset>
	</div>
</layout:body>

