function DataFields ({datafields}) {
	console.log(datafields);
	return datafields.map( datafield =>
		<fieldset className="block" data-datafield={datafield.datafield}>
			<legend>{datafield.datafield} - { _(`marc.bibliographic.datafield.${datafield.datafield}`)}</legend>
			<div class="buttons">
					<span className="cancel-datafield fa fa-close"></span>
					<span className="save-datafield fa fa-check"></span>
					<span className="trash-datafield fa fa-trash-o"></span>
					<span className="edit-datafield fa fa-pencil"></span>
					<span className="move-datafield fa fa-bars"></span>
				</div>
			<div className="edit_area"></div>
		</fieldset>
	);
}

const domContainer = document.querySelector('#datafields');
ReactDOM.render(
	<DataFields
		datafields={CatalogingInput.formFields}/>,
	domContainer);