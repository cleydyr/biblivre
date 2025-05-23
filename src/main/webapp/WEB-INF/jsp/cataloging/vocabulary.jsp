<%@ page import="biblivre.marc.MaterialType" %>
<%@ page import="biblivre.cataloging.enums.RecordType" %>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="static" uri="/WEB-INF/tlds/static.tld" %>

<layout:head>
	<static:stylesheet fileName="biblivre.search.css" />
	<static:stylesheet fileName="biblivre.cataloging.css" />

	<static:script fileName="biblivre.search.js" />
	<static:script fileName="biblivre.cataloging.search.js" />

	<static:script fileName="biblivre.input.js" />
	<static:script fileName="biblivre.cataloging.input.js" />

	<static:script fileName="${requestScope.vocabulariesCacheFileName}" />

	<static:script fileName="zebra_datepicker.js" />
	<static:stylesheet fileName="zebra.bootstrap.css" />

	<script type="text/javascript">
		var CatalogingSearch = CreateSearch(CatalogingSearchClass, {
			type: 'cataloging.vocabulary',
			root: '#vocabulary',
			enableTabs: true,
			enableHistory: true
		});

		CatalogingInput.type = 'cataloging.vocabulary';
		CatalogingInput.root = '#vocabulary';

		CatalogingInput.search = CatalogingSearch;
		CatalogingInput.defaultMaterialType = 'vocabulary';

	</script>
</layout:head>

<layout:body banner="true">
	<div id="vocabulary">

		<div id="database_selection">
			<div class="title"><i18n:text key="cataloging.database.title" />: </div>
			<select name="database" id="database_selection_combo" class="combo combo_auto_size" onchange="Core.trigger(CatalogingInput.type + 'cataloging-database-change');">
				<option value="main"><i18n:text key="cataloging.database.main" /></option>
				<option value="work"><i18n:text key="cataloging.database.work" /></option>
				<option value="private"><i18n:text key="cataloging.database.private" /></option>
				<option value="trash"><i18n:text key="cataloging.database.trash" /></option>
			</select>
			<div class="buttons">
				<a class="button center" id="new_record_button" onclick="CatalogingInput.newRecord();"><i18n:text key="cataloging.bibliographic.button.new" /></a>
			</div>
			<div class="count" id="database_count"></div>

			<div class="clear"></div>
		</div>

		<div class="page_title">
			<div class="search_icon"></div>

			<div class="simple_search text">
				<i18n:text key="search.vocabulary.simple_search" />
			</div>

			<div class="clear"></div>
		</div>

		<div class="page_navigation">
			<a href="javascript:void(0);" class="button paging_button back_to_search" onclick="CatalogingSearch.closeResult();"><i18n:text key="search.common.back_to_search" /></a>

			<div class="fright">
				<a href="javascript:void(0);" class="button paging_button paging_button_prev" onclick="CatalogingSearch.previousResult();"><i18n:text key="search.common.previous" /></a>
				<span class="search_count"></span>
				<a href="javascript:void(0);" class="button paging_button paging_button_next" onclick="CatalogingSearch.nextResult();"><i18n:text key="search.common.next" /></a>
			</div>

			<div class="clear"></div>
		</div>

		<div class="selected_highlight"></div>
		<textarea class="selected_highlight_template template"><!--
			<div class="buttons">
				<div class="view">
					<a class="button center" onclick="CatalogingInput.editRecord('{$T.id}');"><i18n:text key="cataloging.bibliographic.button.edit" /></a>
					<a class="danger_button center" onclick="CatalogingInput.deleteRecord('{$T.id}');"><i18n:text key="cataloging.bibliographic.button.delete" /></a>
				</div>

				<div class="edit">
					<a class="main_button center" onclick="CatalogingInput.saveRecord();"><i18n:text key="cataloging.bibliographic.button.save" /></a>
					<a class="button center" onclick="CatalogingInput.saveRecord(true);"><i18n:text key="cataloging.bibliographic.button.save_as_new" /></a>
					<a class="button center" onclick="CatalogingInput.cancelEdit();"><i18n:text key="cataloging.bibliographic.button.cancel" /></a>
				</div>

				<div class="new">
					<a class="main_button center" onclick="CatalogingInput.saveRecord();"><i18n:text key="cataloging.bibliographic.button.save" /></a>
					<a class="button center" onclick="CatalogingInput.cancelEdit();"><i18n:text key="cataloging.bibliographic.button.cancel" /></a>
				</div>
			</div>
			<div class="record">
				{#if $T.term_te}<label><i18n:text key="cataloging.vocabulary.term.te" /></label>: {$T.term_te}<br/>{#/if}
				{#if $T.term_up}<label><i18n:text key="cataloging.vocabulary.term.up" /></label>: {$T.term_up}<br/>{#/if}
				{#if $T.term_tg}<label><i18n:text key="cataloging.vocabulary.term.tg" /></label>: {$T.term_tg}<br/>{#/if}
				{#if $T.term_vt_ta}<label><i18n:text key="cataloging.vocabulary.term.ta" /></label>: {$T.term_vt_ta}<br/>{#/if}
				<label><i18n:text key="search.bibliographic.id" /></label>: {$T.id}<br/>
			</div>
		--></textarea>

		<div class="selected_record tabs">
			<ul class="tabs_head">
				<li class="tab" onclick="Core.changeTab(this, CatalogingSearch);" data-tab="record"><i18n:text key="cataloging.tabs.brief" /></li>
				<li class="tab" onclick="Core.changeTab(this, CatalogingSearch);" data-tab="form"><i18n:text key="cataloging.tabs.form" /></li>
				<li class="tab" onclick="Core.changeTab(this, CatalogingSearch);" data-tab="marc"><i18n:text key="cataloging.tabs.marc" /></li>
			</ul>

			<div class="tabs_body">
				<div class="tab_body" data-tab="record">
					<div id="biblivre_record"></div>
					<textarea id="biblivre_record_template" class="template"><!--
						<input type="hidden" name="material_type" value="{$T.material_type}"/>
						<table class="record_fields">
							{#foreach $T.fields as field}
								<tr>
									<td class="label">{Translations.get('cataloging.tab.record.custom.field_label.vocabulary_' + $T.field.datafield)}:</td>
									<td class="value">{$T.field.value}</td>
								</tr>
							{#/for}
						</table>
					--></textarea>
				</div>

				<div class="tab_body" data-tab="form">
					<div class="biblivre_form_body">
						<div class="field">
							<div class="clear"></div>
						</div>
					</div>
				</div>

				<div class="tab_body" data-tab="marc">
					<div class="biblivre_marc_body">
						<div class="field">
							<div class="clear"></div>
						</div>
					</div>
				</div>
			</div>

			<div class="tabs_extra_content">
				<div class="tab_extra_content biblivre_record_extra" data-tab="record">
					<textarea id="biblivre_record_textarea"></textarea>
				</div>

				<div class="tab_extra_content biblivre_form" data-tab="form">
					<div id="biblivre_form"></div>
				</div>

				<div class="tab_extra_content biblivre_marc" data-tab="marc">
					<fieldset>
						<div id="biblivre_marc"></div>
						<textarea id="biblivre_marc_template" class="template"><!--
								<table class="record_fields readonly_text">
									{#foreach $T.fields as field}
										<tr>
											<td class="label">{$T.field.field}</td>
											<td class="value">{$T.field.value}</td>
										</tr>
									{#/for}
								</table>
						--></textarea>
						<textarea id="biblivre_marc_textarea"></textarea>
					</fieldset>
				</div>
			</div>

			<div class="footer_buttons">
				<div class="edit">
					<a class="main_button center" onclick="CatalogingInput.saveRecord();"><i18n:text key="cataloging.bibliographic.button.save" /></a>
					<a class="button center" onclick="CatalogingInput.saveRecord(true);"><i18n:text key="cataloging.bibliographic.button.save_as_new" /></a>
					<a class="button center" onclick="CatalogingInput.cancelEdit();"><i18n:text key="cataloging.bibliographic.button.cancel" /></a>
				</div>

				<div class="new">
					<a class="main_button center" onclick="CatalogingInput.saveRecord();"><i18n:text key="cataloging.bibliographic.button.save" /></a>
					<a class="button center" onclick="CatalogingInput.cancelEdit();"><i18n:text key="cataloging.bibliographic.button.cancel" /></a>
				</div>
			</div>
		</div>

		<div class="search_box">
			<div class="simple_search submit_on_enter">
				<div class="wide_query">
					<input type="text" name="query" class="big_input auto_focus" placeholder="<i18n:text key="search.user.simple_term_title" />"/>
				</div>
				<div class="buttons">
					<a class="main_button arrow_right" onclick="CatalogingSearch.search('simple');"><i18n:text key="search.common.button.list_all" /></a>
				</div>
			</div>
		</div>

		<div class="selected_results_area">
		</div>

		<textarea class="selected_results_area_template template"><!--
			{#if $T.length > 0}
				<fieldset class="block">
					<legend>{_p('cataloging.bibliographic.selected_records', $T.length)}</legend>
					<ul>
						{#foreach $T as record}
							<li rel="{$T.record.id}">
								{#if $T.record.term_te}
									{$T.record.term_te}
								{#elseif $T.record.term_up}
									{$T.record.term_up}
								{#elseif $T.record.term_tg}
									{$T.record.term_tg}
								{#elseif $T.record.term_vt_ta}
									{$T.record.term_vt_ta}
								{#/if}
								<a class="xclose" onclick="CatalogingSearch.unselectRecord({$T.record.id});">&times;</a>
							</li>
						{#/for}
					</ul>
					<div class="buttons">
						<select name="move" class="combo combo_hide_empty_value" onchange="CatalogingInput.confirmMoveSelectedRecords(this.value);">
							<option value=""><i18n:text key="cataloging.bibliographic.button.move_records" /></option>
							<option value="main"><i18n:text key="cataloging.database.main_full" /></option>
							<option value="work"><i18n:text key="cataloging.database.work_full" /></option>
							<option value="private"><i18n:text key="cataloging.database.private_full" /></option>
							<option value="trash"><i18n:text key="cataloging.database.trash_full" /></option>
						</select>
						<a class="button center" onclick="CatalogingSearch.exportSelectedRecords();"><i18n:text key="cataloging.bibliographic.button.export_records" /></a>
					</div>
				</fieldset>
			{/#if}
		--></textarea>

		<div class="search_results_area">
			<div class="search_ordering_bar">

				<div class="search_indexing_groups"></div>
				<textarea class="search_indexing_groups_template template"><!--
					{#foreach $T.search.indexing_group_count as group_count}
						{#foreach $T.indexing_groups as group}
							{#if $T.group_count.group_id == $T.group.id}
								{#if $T.group.id == CatalogingSearch.lastPagingParameters.indexing_group}
									<div class="group selected">
										<span class="name">{Translations.get('cataloging.vocabulary.indexing_groups.' + ($T.group.id ? $T.group.translation_key : 'total'))}</span>
										<span class="value">({_f($T.group_count.result_count)})</span>
									</div>
								{#else}
									<div class="group">
										<a href="javascript:void(0);" onclick="CatalogingSearch.changeIndexingGroup('{$T.group.id}');">{Translations.get('cataloging.vocabulary.indexing_groups.' + ($T.group.id ? $T.group.translation_key : 'total'))}</a>
										<span class="value">({_f($T.group_count.result_count)})</span>
									</div>
								{#/if}
							{#/if}
						{#/for}
						{#if !$T.group_count$last} <div class="hspacer">|</div> {#/if}
					{#/for}
				--></textarea>

				<div class="search_sort_by"></div>
				<textarea class="search_sort_by_template template"><!--
					<i18n:text key="search.common.sort_by" />:
					<select class="combo search_sort_combo combo_auto_size combo_align_right" onchange="CatalogingSearch.changeSort(this.value);">
						{#foreach $T.indexing_groups as group}
							{#if $T.group.sortable}
								<option value="{$T.group.id}"{#if ((CatalogingSearch.lastPagingParameters.sort == $T.group.id) || (!CatalogingSearch.lastPagingParameters.sort && $T.group.default_sort))} selected="selected" {#/if}>{Translations.get('cataloging.vocabulary.indexing_groups.' + $T.group.translation_key)}</option>
							{#/if}
						{#/for}
					</select>
				--></textarea>

				<div class="clear"></div>
			</div>

			<div class="search_loading_indicator loading_indicator"></div>


			<div class="select_bar">
				<a class="button center" onclick="CatalogingSearch.selectPageResults();"><i18n:text key="cataloging.bibliographic.button.select_page" /></a>
			</div>
			<div class="paging_bar"></div>
			<div class="clear"></div>

			<div class="search_results_box">
				<div class="search_results"></div>
				<textarea class="search_results_template template"><!--
					{#foreach $T.data as record}
						<div class="result {#cycle values=['odd', 'even']} {CatalogingInput.getOverlayClass($T.record)}" rel="{$T.record.id}">
							<div class="result_overlay"><div class="text">{CatalogingInput.getOverlayText($T.record)}</div></div>
							<div class="buttons">
								<a class="button center" rel="open_item" onclick="CatalogingSearch.openResult('{$T.record.id}');"><i18n:text key="search.bibliographic.open_item_button" /></a>
								<a class="button center" rel="select_item" onclick="CatalogingSearch.selectRecord('{$T.record.id}');"><i18n:text key="cataloging.bibliographic.button.select_item" /></a>
								<a class="danger_button center" onclick="CatalogingInput.deleteRecord('{$T.record.id}');"><i18n:text key="cataloging.bibliographic.button.delete" /></a>
							</div>
							<div class="record">
								{#if $T.record.term_te}<label><i18n:text key="cataloging.vocabulary.term.te" /></label>: {$T.record.term_te}<br/>{#/if}
								{#if $T.record.term_up}<label><i18n:text key="cataloging.vocabulary.term.up" /></label>: {$T.record.term_up}<br/>{#/if}
								{#if $T.record.term_tg}<label><i18n:text key="cataloging.vocabulary.term.tg" /></label>: {$T.record.term_tg}<br/>{#/if}
								{#if $T.record.term_vt_ta}<label><i18n:text key="cataloging.vocabulary.term.ta" /></label>: {$T.record.term_vt_ta}<br/>{#/if}
								<label><i18n:text key="search.bibliographic.id" /></label>: {$T.record.id}<br/>
							</div>
							<div class="clear"></div>
						</div>
					{#/for}
				--></textarea>
			</div>

			<div class="paging_bar"></div>
		</div>
	</div>

</layout:body>
