/**
 *  Este arquivo é parte do Biblivre5.
 *
 *  Biblivre5 é um software livre; você pode redistribuí-lo e/ou
 *  modificá-lo dentro dos termos da Licença Pública Geral GNU como
 *  publicada pela Fundação do Software Livre (FSF); na versão 3 da
 *  Licença, ou (caso queira) qualquer versão posterior.
 *
 *  Este programa é distribuído na esperança de que possa ser  útil,
 *  mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 *  MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 *  Licença Pública Geral GNU para maiores detalhes.
 *
 *  Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 *  com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 *
 *  @author Alberto Wagner <alberto@biblivre.org.br>
 *  @author Danniel Willian <danniel@biblivre.org.br>
 *
 */

var OrderInput = OrderInput || {};

var OrderSearchClass = {

	initialize: function () {
		var me = this;

		this.root.find('.search_results').setTemplateElement(this.root.find('.search_results_template'));
		this.root.find('.selected_results_area').setTemplateElement(this.root.find('.selected_results_area_template'));

		$.History.bind(function (trigger) {
			return me.historyRead(trigger);
		});

		if (!Core.qhs('search')) {
			me.switchToSimpleSearch();
		}

	},
	afterHistoryRead: function (trigger) {
		var query = Core.historyCheckAndSet(trigger, 'query');

		if (query.changed) {
			if (query.value !== null) {
				this.searchTerm({
					query: query.value
				});
			}
		}
	},
	clearResults: function () {
		Core.trigger(this.prefix + 'clear-search');
	},
	simpleSearch: function () {
		var query = this.root.find('.search_box .simple_search :input[name=query]').val();

		var searchParameters = {
			mode: 'simple',
			query: query
		};

		Core.historyTrigger({
			query: query
		});

		this.submit(searchParameters);
	},
	afterDisplayResult: function (config) {
		this.root.find('.search_results .result').fixButtonsHeight();
	},
	searchTerm: function (obj) {
		var query = $("<div />").html(obj.query).text();
		this.root.find('.search_box .simple_search :input[name=query]').val(query);

		this.root.find('.search_box .main_button:visible').trigger('click');
	},
	loadRecord: function (record, callback) {
		this.clearAll();
		this.selectedRecord = record;

		if (this.enableTabs) {
			Core.changeTab(this.selectedTab || 'form', this);
		}
	},
	tabHandler: function (tab, params) {
		params = params || {};
		data = params.data || this.selectedRecord;

		this.selectedTab = tab;
		switch (tab) {
			case 'form':
				this.loadOrderForm(data, params);
				break;
		}
	},
	clearTab: function (tab) {
		switch (tab) {
			case 'form':
				$('#biblivre_order_form').empty().data('loaded', false);
				$('#biblivre_order_form_body').empty();
				$('#biblivre_order_info_form').empty();
				break;
		}
	},
	clearAll: function () {
		this.clearTab('form');
	},
	loadOrderForm: function (record, params) {
		var div = $('#biblivre_order_form_body');

		if (div.data('loaded')) {
			if (params.force) {
				this.clearTab('form');
			} else {
				return;
			}
		}

		div.processTemplate(record);
		OrderInput.listQuotations($('#biblivre_order_form_body select[name="supplier"]').val());
		$('#biblivre_order_form').processTemplate(record);
		$('#biblivre_order_info_form').processTemplate(record);

		if (!params.keepEditing && OrderInput) {
			OrderInput.setAsReadOnly();
		}
	}
};