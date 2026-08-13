/**
 * Cutter-Sanborn author code helper for bibliographic MARC form (090 $b).
 */
const CatalogingCutter = {
	_table: null,

	_articles: new Set([
		'a',
		'an',
		'as',
		'el',
		'la',
		'las',
		'los',
		'o',
		'os',
		'the',
		'um',
		'uma',
		'umas',
		'un',
		'una',
		'uns',
	]),

	getTable() {
		if (!this._table) {
			if (!window.CutterSanborn?.CutterSanbornTableFactory) {
				return null;
			}
			this._table = window.CutterSanborn.CutterSanbornTableFactory.createTable();
		}
		return this._table;
	},

	/**
	 * Parse MARC personal name (100 $a): "Surname, Given Name" or bare surname.
	 */
	parsePersonalName(marcName) {
		const trimmed = String(marcName ?? '').trim();
		if (!trimmed) {
			return { surname: '', givenName: '' };
		}

		const commaIndex = trimmed.indexOf(',');
		if (commaIndex < 0) {
			return { surname: trimmed, givenName: '' };
		}

		return {
			surname: trimmed.slice(0, commaIndex).trim(),
			givenName: trimmed.slice(commaIndex + 1).trim(),
		};
	},

	/**
	 * Corporate / congress names: first significant word as surname lookup key.
	 */
	parseCorporateName(marcName) {
		const trimmed = String(marcName ?? '').trim();
		if (!trimmed) {
			return { surname: '', givenName: '' };
		}

		const words = trimmed.split(/\s+/);
		let surname = words[0] ?? '';

		for (const rawWord of words) {
			const word = this._lettersOnly(rawWord);
			if (!word || this._articles.has(word.toLowerCase())) {
				continue;
			}
			surname = word;
			break;
		}

		return { surname, givenName: '' };
	},

	_lettersOnly(text) {
		return String(text ?? '').replace(/[^A-Za-z\u00C0-\u024F]/g, '');
	},

	_firstLetter(text) {
		return String(text ?? '').match(/[A-Za-z\u00C0-\u024F]/)?.[0] ?? '';
	},

	/**
	 * First significant letter of the title (work letter), lowercase.
	 * Prefers MARC 245 indicator 2 non-filing character count when > 0.
	 */
	workLetter(title, nonFilingChars) {
		let text = String(title ?? '');
		const skip = Number.parseInt(nonFilingChars, 10);

		if (!Number.isNaN(skip) && skip > 0) {
			text = text.slice(skip);
		} else {
			text = this._stripLeadingArticles(text);
		}

		return this._firstLetter(text).toLowerCase();
	},

	_stripLeadingArticles(title) {
		const text = String(title ?? '').trim();
		const match = text.match(/^(\S+)\s+(.*)$/);
		if (!match) {
			return text;
		}

		const firstWord = this._lettersOnly(match[1]).toLowerCase();
		if (this._articles.has(firstWord)) {
			return match[2];
		}
		return text;
	},

	/**
	 * Build full author code: surname letter + Cutter number + work letter.
	 * Returns null when inputs or table lookup are insufficient.
	 */
	buildAuthorCode(surname, givenName, title, nonFilingChars) {
		const cleanSurname = String(surname ?? '').trim();
		if (!cleanSurname) {
			return null;
		}

		const surnameLetter = this._firstLetter(cleanSurname).toUpperCase();
		if (!surnameLetter) {
			return null;
		}

		const work = this.workLetter(title, nonFilingChars);
		if (!work) {
			return null;
		}

		const table = this.getTable();
		if (!table) {
			return null;
		}

		const number = table.callNumber(
			cleanSurname.toLowerCase(),
			String(givenName ?? '').toLowerCase(),
		);
		if (!number || number < 0) {
			return null;
		}

		return `${surnameLetter}${number}${work}`;
	},

	_readFirstSubfield(formRoot, datafield, subfield) {
		const input = formRoot
			.find(`fieldset.datafield[data="${datafield}"]`)
			.first()
			.find(`:input[name="${subfield}"]`)
			.first();
		return String(input.val() ?? '').trim();
	},

	_readIndicator(formRoot, datafield, indicatorName) {
		const select = formRoot
			.find(`fieldset.datafield[data="${datafield}"]`)
			.first()
			.find(`select[name="${indicatorName}"]`)
			.first();
		return String(select.val() ?? '').trim();
	},

	_resolveAuthorParts(formRoot) {
		const personal = this._readFirstSubfield(formRoot, '100', 'a');
		if (personal) {
			return this.parsePersonalName(personal);
		}

		const corporate = this._readFirstSubfield(formRoot, '110', 'a');
		if (corporate) {
			return this.parseCorporateName(corporate);
		}

		const congress = this._readFirstSubfield(formRoot, '111', 'a');
		if (congress) {
			return this.parseCorporateName(congress);
		}

		return { surname: '', givenName: '' };
	},

	_resolveAuthorCodeInput(trigger) {
		const $trigger = $(trigger);
		const fromSubfield = $trigger.closest('.subfield').find(':input[name="b"]').first();
		if (fromSubfield.length) {
			return fromSubfield;
		}

		return $trigger
			.closest('fieldset.datafield[data="090"]')
			.find(':input[name="b"]')
			.first();
	},

	_resolveFormRoot(trigger) {
		const fromTrigger = $(trigger).closest('#biblivre_form');
		return fromTrigger.length ? fromTrigger : $('#biblivre_form');
	},

	_applyAuthorCode(authorCodeInput, authorCode) {
		authorCodeInput.val(authorCode);
	},

	fillAuthorCode(trigger) {
		const formRoot = this._resolveFormRoot(trigger);
		const authorCodeInput = this._resolveAuthorCodeInput(trigger);

		if (!authorCodeInput.length) {
			Core.msg({
				message: Translations.get('cataloging.bibliographic.cutter.not_found'),
				message_level: 'error',
			});
			return;
		}

		const authorParts = this._resolveAuthorParts(formRoot);
		if (!authorParts.surname) {
			Core.msg({
				message: Translations.get('cataloging.bibliographic.cutter.missing_author'),
				message_level: 'warning',
			});
			return;
		}

		const title = this._readFirstSubfield(formRoot, '245', 'a');
		if (!title) {
			Core.msg({
				message: Translations.get('cataloging.bibliographic.cutter.missing_title'),
				message_level: 'warning',
			});
			return;
		}

		const nonFilingChars = this._readIndicator(formRoot, '245', 'ind2');
		const authorCode = this.buildAuthorCode(
			authorParts.surname,
			authorParts.givenName,
			title,
			nonFilingChars,
		);

		if (!authorCode) {
			Core.msg({
				message: Translations.get('cataloging.bibliographic.cutter.not_found'),
				message_level: 'error',
			});
			return;
		}

		const currentValue = String(authorCodeInput.val() ?? '').trim();
		if (currentValue) {
			Core.popup({
				title: Translations.get('cataloging.bibliographic.cutter.confirm_overwrite_title'),
				description: Translations.get(
					'cataloging.bibliographic.cutter.confirm_overwrite_message',
				),
				okHandler: () => this._applyAuthorCode(authorCodeInput, authorCode),
			});
			return;
		}

		this._applyAuthorCode(authorCodeInput, authorCode);
	},
};
