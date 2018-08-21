import React, {Component} from 'react';
import ReactDOM from 'react-dom';

function MessageBox({translations, messageKey, actionKey, actionQuery, level}) {
	return (
			<div className={`message sticky ${level}`}>
			<div>
				{translations[messageKey]}
				<a href={actionQuery} className="fright">
					{translations[actionKey]}
				</a>
			</div>
		</div>
	);
}
const changePasswordMessageContainer = document.querySelector('#change_passsword_message_container');

changePasswordMessageContainer && ReactDOM.render(
	<MessageBox
		translations={Translations.translations}
		messageKey='warning.change_password'
		actionKey='warning.fix_now'
		actionQuery='?action=administration_password'
		level='error'
	/>,
	changePasswordMessageContainer);

class DataFields extends Component {
	render() {
		const {formFields, selectedRecordType} = this.props;
		const enabledFields = formFields[selectedRecordType];
		const datafields = ld.sortBy(enabledFields, 'sortOrder');

		FormCustomization.indexedDatafields = ld.keyBy(FormCustomization.datafields, 'datafield');

		return datafields.map( datafield =>
			(<fieldset className="block" data-datafield={datafield.datafield}>
				<legend>{datafield.datafield} - { _(`marc.bibliographic.datafield.${datafield.datafield}`)}</legend>
				<div class="buttons">
						<span className="cancel-datafield fa fa-close"></span>
						<span className="save-datafield fa fa-check"></span>
						<span className="trash-datafield fa fa-trash-o"></span>
						<span className="edit-datafield fa fa-pencil"></span>
						<span className="move-datafield fa fa-bars"></span>
					</div>
				<div className="edit_area"></div>
			</fieldset>)
		);
	};
}

const datafields = document.querySelector('#datafields');
datafields && ReactDOM.render(
	<DataFields
		formFields={FormCustomization.formFields}
		selectedRecordType='bibliographic'/>,
	datafields);