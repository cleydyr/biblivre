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
var UserTypeInput = new Input({
	initialize: function() {
		$('#biblivre_usertype_form_body').setTemplateElement('biblivre_usertype_form_body_template');

		Core.subscribe(this.prefix + 'record-deleted', function(e, id) {
			if (this.search.lastSearchResult && this.search.lastSearchResult[id]) {
				this.search.lastSearchResult[id].deleted = true;

				this.search.processResultTemplate();
			}

			this.search.closeResult();
		}, this);
	},
	clearTab: function(tab) {
		switch (tab) {
			case 'form':
				this.setAsEditable();
				break;
		}
	},
	clearAll: function() {
		this.clearTab('form');
	},
	setAsReadOnly: function() {
		var root = $('#biblivre_usertype_form_body');

		root.find('input:text, textarea:not(.template)').each(function() {
			var input = $(this);

			var value = input.val();
			input.after($('<div class="readonly_text"></div>').text(value)).addClass('readonly_hidden');
		});
	},
	setAsEditable: function(tab) {
		var root = $('#biblivre_usertype_form_body');

		root.find('.readonly_text').remove();
		root.find('.readonly_hidden').removeClass('readonly_hidden');
	},
	editing: false,
	recordIdBeingEdited: null,
	editRecord: function(id) {
		if (this.editing) {
			return;
		}

		if (Core.trigger(this.prefix + 'edit-record-start', id) === false) {
			return;
		}

		this.editing = true;
		this.recordIdBeingEdited = id;

		var tab = this.search.selectedTab;
		if (tab != 'form') {
			tab = 'form';
		} else {
			this.setAsEditable('form');
		}

		Core.changeTab(tab, this.search, { keepEditing: true, skipConvert: true });
	},
	getNewRecord: function() {
		return {
			id: ' '
		};
	},
	getSaveRecord: function(saveAsNew) {
		var body = $('#biblivre_usertype_form_body');

		var params = {};

		return $.extend(params, {
			oldId: this.recordIdBeingEdited || 0,
			id: (saveAsNew) ? 0 : this.recordIdBeingEdited,
			name: body.find(':input[name=name]').val(),
			description: body.find(':input[name=description]').val(),
			lending_limit: body.find(':input[name=lending_limit]').val(),
			reservation_limit: body.find(':input[name=reservation_limit]').val(),
			lending_time_limit: body.find(':input[name=lending_time_limit]').val(),
			reservation_time_limit: body.find(':input[name=reservation_time_limit]').val(),
			fine_value: Globalize.parseFloat(body.find(':input[name=fine_value]').val())
		});
	},
	deleteRecordTitle: function() {
		return Translations.get(this.type + '.confirm_delete_record_title.forever');
	},
	deleteRecordQuestion: function() {
		return Translations.get(this.type + '.confirm_delete_record_question.forever');
	},
	deleteRecordConfirm: function() {
		return Translations.get(this.type + '.confirm_delete_record.forever');
	},
	getDeleteRecord: function(id) {
		return {
			id: id,
		};
	},
	getOverlayClass: function(record) {
		if (record.deleted) {
			return 'overlay_error';
		}

		return '';
	},
	getOverlayText: function(record) {
		if (record.deleted) {
			return Translations.get('common.deleted');
		}

		return '';
	}
});
