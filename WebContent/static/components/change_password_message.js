function MessageBox({translations, messageKey, actionKey, actionQuery, level}) {
	return (
			<div className={`message sticky ${level}`}>
			<div>
				{translations[messageKey]}
				<a href={actionQuery} class="fright">
					{translations[actionKey]}
				</a>
			</div>
		</div>
	);
}

const domContainer = document.querySelector('#change_passsword_message_container');
ReactDOM.render(
	<MessageBox
		translations={Translations.translations}
		messageKey='warning.change_password'
		actionKey='warning.fix_now'
		actionQuery='?action=administration_password'
		level='error'
	/>, domContainer);

