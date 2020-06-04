package biblivre.update.v5_0_0;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import biblivre.cataloging.enums.RecordType;
import biblivre.core.translations.Translations;
import biblivre.update.UpdateService;

public class Update implements UpdateService {

	@Override
	public void doUpdate(Connection connection) {
		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "menu.administration_brief_customization",
				"Personalização de Resumo Catalográfico");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "menu.administration_brief_customization",
				"Catalographic Summary Customization");

		Translations.addOrReplaceSingleTranslation(
				"es", "menu.administration_brief_customization",
				"Personalización del Resumen Catalográfico");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "menu.administration_form_customization",
				"Personalização de Formulário Catalográfico");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "menu.administration_form_customization",
				"Catalographic Form Customization");

		Translations.addOrReplaceSingleTranslation(
				"es", "menu.administration_form_customization",
				"Personalización del Formulario Catalográfico");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.permissions.items.administration_customization",
				"Personalização");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.permissions.items.administration_customization",
				"Customization");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.permissions.items.administration_customization",
				"Personalización");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.separators.space-dash-space",
				"Espaço - hífen - espaço");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.separators.space-dash-space",
				"Blank - dash - blank");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.separators.space-dash-space",
				"Espacio - guión - espacio");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.separators.comma-space",
				"Vírgula - espaço");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.separators.comma-space",
				"Comma - blank");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.separators.comma-space",
				"Coma - espacio");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.separators.dot-space",
				"Ponto - espaço");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.separators.dot-space", "Dot - blank");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.separators.dot-space", "Punto - espacio");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.separators.colon-space",
				"Dois pontos - espaço");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.separators.colon-space",
				"Colon - blank");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.separators.colon-space",
				"Dos Puntos - espacio");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.separators.semicolon-space",
				"Ponto e vírgula - espaço");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.separators.semicolon-space",
				"Semicolon - blank");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.separators.semicolon-space",
				"Punto y coma - espacio");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.aggregators.left-parenthesis",
				"Abre parênteses");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.aggregators.left-parenthesis",
				"Left parenthesis");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.aggregators.left-parenthesis",
				"Paréntesis izquierdo");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.aggregators.right-parenthesis",
				"Fecha parênteses");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.aggregators.right-parenthesis",
				"Right parenthesis");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.aggregators.right-parenthesis",
				"Paréntesis derecho");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.confirm_disable_datafield_title",
				"Desabilitar a exibição");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.confirm_disable_datafield_title",
				"Hide field");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.confirm_disable_datafield_title",
				"Ocultar campo");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.confirm_disable_datafield_question",
				"Marcando esta opção você estará escondendo o campo na aba de Resumo " +
					"Catalográfico. Você poderá exibir o mesmo novamente depois, caso mude de " +
					"idéia.");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.confirm_disable_datafield_question",
				"By selecting this option, you'll be hiding the Field from the Catalographic "  +
					"Summary tab. You'll be able to show the field back if you change your mind.");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.confirm_disable_datafield_question",
				"Al activar esta opción se esconden el campo en lo Resumen Catalográfico. Usted " +
					"será capaz de mostrar el campo de vuelta si cambia de opinión.");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.confirm_disable_datafield_confirm",
				"Tem certeza que deseja remover este campo do Resumo Catalográfico?");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.confirm_disable_datafield_confirm",
				"Are you sure you want to hide this field from the Catalographic Summary tab?");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.confirm_disable_datafield_confirm",
				"¿Seguro que quieres ocultar este campo desde lo Resumen Catalográfico?");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.page_help",
				"<p>A rotina de Personalizaçao de Resumo Catalográfico permite configurar quais " +
					"campos e subcampos MARC serão apresentados nas rotinas de Catalogação " +
					"Bibliográfica, de Autoridades e de Vocabulários.  Os campos e subcampos " +
					"configurados aqui serão apresentados na aba de Resumo Catalográfico nas " +
					"rotinas de Catalogação. Você poderá configurar a ordem dos campos e " +
					"subcampos, assim como os separadores que irão aparecer entre os subcampos." +
					"</p><p>Os campos exibidos nesta tela são os campos disponíveis no " +
					"Formulário Catalográfico. Para criar novos campos, ou alterar seus " +
					"subcampos, utilize a tela de <b>Personalização de Formulário Catalográfico." +
					"</b></p>");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.page_help",
				"<p>The Catalographic Summary Customization page lets you customize which MARC " +
					"Tags and Subfields will be displayed in the Cataloging pages. The Tags and " +
					"Subfields customized in this page will be displayed in the Catalographic " +
					"Summary tabs in the Cataloging pages. You can customize the order for the " +
					"Tags and Subfields, and also customize the separators or aggregators for " +
					"the Subfields.</p><p>All the Tags and Subfields displayed here are the ones " +
					"available in the Catalographic Form page. To create new Tags or Subfields, " +
					"go to the <b>Catalographic Form Customization</b> page.</p>");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.page_help",
				"<p>La página Personalización de lo Resumen Catalográfico le permite " +
					"personalizar cual Campos y Subcampos MARC se mostrarán en las páginas de " +
					"Catalogación. Los Campos y Subcampos personalizados en esta página se " +
					"mostrarán en las fichas de lo Resumen catalográfico en las páginas de " +
					"Catalogación. Usted puede personalizar el orden de los Campos y Subcampos, " +
					"y también personalizar los separadores o agregadores del subcampos.</p><p>" +
					"Todas los Campos y Subcampos que se muestra aquí son los que están " +
					"disponibles en la página de Formulario Catalográfico. Para crear nuevas " +
					"etiquetas o subcampos, vaya a la <b>Personalización del Formulario " +
					"Catalográfico</b>.</p>");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.select_record_type",
				"Selecione o Tipo de Registro");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.select_record_type",
				"Select the Record Type");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.select_record_type",
				"Seleccione el Tipo de Registro");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.biblio", "Registro Bibliográfico");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.biblio", "Bibliographic Record");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.biblio", "Registro Bibliográfico");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.authorities",
				"Registro de Autoridades");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.authorities", "Authorities Record");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.authorities", "Registro de Autoridad");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.vocabulary",
				"Registro de Vocabulário");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.vocabulary", "Vocabulary record");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.vocabulary", "Registro de Vocabulario");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.subfields_title", "Subcampos");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.subfields_title", "Subfields");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.subfields_title", "Subcampo");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.separators_title",
				"Separadores de subcampo");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.separators_title",
				"Subfield separators");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.separators_title",
				"Separadores de subcampo");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.aggregators_title",
				"Agregadores de subcampo");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.aggregators_title",
				"Subfield aggregators");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.aggregators_title",
				"Agregadores de subcampo");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.page_help",
				"<p>A rotina de Personalização de Formulário Catalográfico permite configurar " +
					"quais Campos, Subcampos e Indicadores MARC serão apresentados nas rotinas " +
					"de Catalogação Bibliográfica, de Autoridades e de Vocabulários. Os Campos, " +
					"Subcampos e Indicadores configurados aqui serão apresentados na aba de " +
					"Formulário Catalográfico nas rotinas de Catalogação. Você poderá configurar " +
					"a ordem dos Campos, Subcampos e Indicadores, assim como editar cada Campo, " +
					"adicionando ou removendo Subcampos e Indicadores, ou alterando os textos " +
					"dos elementos MARC.</p>");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.page_help",
				"<p>The Catalographic Form Customization allows you to configure which MARC " +
					"Tags, Subfields and Indicators will be displayed in the Cataloging pages. " +
					"The Tags, Subfields and Indicators set here will be displayed in the " +
					"Cataloging Form tab in the Cataloging pages. You can customize the order of " +
					"the Tags, Subfields and Indicators, as well as edit each Tag by adding or "+
					"removing Subfields and Indicators, or changing the text of the MARC " +
					"elements.</p>");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.page_help",
				"<p>La página Personalización del Formulario Catalográfico le permite configurar " +
					"cual Campos, Subcampos e Indicadores MARC se mostrarán en las páginas de " +
					"catalogación. Los Campos, Subcampos e Indicadores establecidos aquí se " +
					"mostrarán en la pestaña Formulario Catalografico en las páginas de " +
					"Catalogación. Puede personalizar el orden de los Campos, Subcampos e " +
					"Indicadores, así como editar cada etiqueta mediante la adición o " +
					"eliminación de Subcampos e Indicadores, o cambiando el texto de los " +
					"elementos MARC.</p>");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.field", "Campo MARC");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.field", "MARC Tag");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.field", "Campo MARC");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.field_name", "Nome do Campo");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.field_name", "Tag Name");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.field_name", "Nombre del Campo");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.field_repeatable", "Repetível");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.field_repeatable", "Repeatable");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.field_repeatable", "Repetible");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.field_collapsed", "Colapsado");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.field_collapsed", "Collapsed");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.field_collapsed", "Colapsado");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.indicator_number", "Indicador");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.indicator_number", "Indicator");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.indicator_number", "Indicador");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.indicator_name", "Nome do indicador");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.indicator_name", "Indicator name");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.indicator_name", "Nombre del indicador");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.indicator_values", "Valores");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.indicator_values", "Values");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.indicator_values", "Valores");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.change_indicators", "Alterar");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.change_indicators", "Change");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.change_indicators", "Cambio");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.material_type", "Tipos de Material");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.material_type", "Material Type");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.material_type", "Tipos de Material");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.subfield", "MARC");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.subfield", "MARC");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.subfield", "MARC");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.subfield_name", "Nome do Subcampo");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.subfield_name", "Subfield name");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.subfield_name", "Nombre del Subcampo");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.subfield_repeatable", "Repetível");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.subfield_repeatable", "Repeatable");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.subfield_repeatable", "Repetible");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.subfield_collapsed", "Oculto");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.subfield_collapsed", "Hidden");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.subfield_collapsed", "Oculto");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.subfield_autocomplete.label",
				"Auto Completar");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.subfield_autocomplete.label",
				"Autocomplete");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.subfield_autocomplete.label",
				"Autocompletar");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.subfield_autocomplete.",
				"Auto Completar");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.subfield_autocomplete.",
				"Autocomplete");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.subfield_autocomplete.",
				"Autocompletar");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.subfield_autocomplete.disabled",
				"Desabilitado");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.subfield_autocomplete.disabled",
				"Disabled");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.subfield_autocomplete.disabled",
				"Inactivo");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.subfield_autocomplete.previous_values",
				"Valores anteriores");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.subfield_autocomplete.previous_values",
				"Previous Values");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.subfield_autocomplete.previous_values",
				"Valores anteriores");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.subfield_autocomplete.fixed_table",
				"Tabela fixa");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.subfield_autocomplete.fixed_table",
				"Fixed Table");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.subfield_autocomplete.fixed_table",
				"Tabla fija");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR",
				"administration.form_customization.subfield_autocomplete." +
						"fixed_table_with_previous_values", "Tabela e Valores");

		Translations.addOrReplaceSingleTranslation(
				"en-US",
				"administration.form_customization.subfield_autocomplete." +
					"fixed_table_with_previous_values", "Table and Values");

		Translations.addOrReplaceSingleTranslation(
				"es",
				"administration.form_customization.subfield_autocomplete." +
					"fixed_table_with_previous_values", "Tabla e Valores");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.subfield_autocomplete.biblio",
				"Bibliográfico");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.subfield_autocomplete.biblio",
				"Bibliographic");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.subfield_autocomplete.biblio",
				"Bibliografico");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.subfield_autocomplete.authorities",
				"Autoridades");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.subfield_autocomplete.authorities",
				"Authorities");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.subfield_autocomplete.authorities",
				"Autoridades");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.subfield_autocomplete.vocabulary",
				"Vocabulário");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.subfield_autocomplete.vocabulary",
				"Vocabulary");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.subfield_autocomplete.vocabulary",
				"Vocabulario");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.translations.error.invalid_language",
				"Idioma em branco ou desconhecido");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.translations.error.invalid_language",
				"The \"language_code\" field is mandatory");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.translations.error.invalid_language",
				"El campo \"language_code\" es obligatorio");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.subfields", "Subcampos");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.subfields", "Subfields");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.subfields", "Subcampos");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.translations.save", "Salvar traduções");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.translations.save", "Save translations");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.translations.save", "Guardar traducciones");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.translations.edit.title", "Editar traduções");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.translations.edit.title", "Edit translations");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.translations.edit.title", "Editar traducciones");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.translations.edit.description",
				"<p>Abaixo você pode editar as traduções sem ter que baixar o arquivo. Esta tela " +
					"é ideal para rápidas alterações em textos do Biblivre. O idioma exibido " +
					"abaixo é o mesmo que está atualmente em uso. Para editar as traduções de " +
					"outro idioma, troque o idioma atual do Biblivre por outro no topo da " +
					"página. Caso você tenha personalizado seu Biblivre na tela de " +
					"Personalizacao, você precisará ajustar os nomes dos campos criados para " +
					"todos os idiomas instalados. Para facilitar nesse trabalho, clique na caixa " +
					"\"Exibir apenas os campos sem tradução\".</p><p>Você pode também adicionar " +
					"um novo idioma diretamente nesta tela. Para tanto, basta alterar o valor " +
					"do campo \"language_code\".</p>");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.translations.edit.description",
				"<p>Below you can edit the translations without downloading the translations " +
					"file. This screen is ideal for rapid changes in Biblivre texts. The " +
					"language displayed below is the same as the one currently in use. To edit " +
					"translations from another language, change the current language at the top " +
					"of the page. If you have customized your Biblivre in the Customization " +
					"screen, you need to adjust the field names created for all languages " +
					"installed. To facilitate this work, click the box \"Display only " +
					"untranslated fields\".</p><p>You can also add a new language directly on " +
					"this screen. To do so, just change the value of the \"language_code\" field." +
					"</p>");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.translations.edit.description",
				"<p>A continuación puede editar las traducciones sin tener que descargar el " +
					"archivo. Esta pantalla es ideal para los rápidos cambios en los textos " +
					"Biblivre. El idioma que se muestra a continuación es la misma que está " +
					"actualmente en uso. Para editar las traducciones de otro idioma, cambie el " +
					"idioma en la parte superior de la página. Si ha personalizado su Biblivre " +
					"en la pantalla de Personalización, es necesario ajustar los nombres de los " +
					"campos creados para todos los idiomas instalados. Para facilitar este " +
					"trabajo, haga click en la casilla \"Mostrar sólo los campos sin traducir\". " +
					"</p><p>También puede añadir un nuevo idioma directamente en esta pantalla. " +
					"Para ello, basta cambiar el valor del campo \"language_code\".</p>");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.translations.edit.filter",
				"Exibir apenas os campos sem tradução");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.translations.edit.filter",
				"Display only untranslated fields");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.translations.edit.filter",
				"Mostrar sólo los campos sin traducir");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.brief_customization.available_fields.description",
				"Os campos abaixo estão configurados no Formulário Catalográfico, porém não " +
				"serão exibidos no Resumo Catalográfico.");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.brief_customization.available_fields.description",
				"Save translations");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.brief_customization.available_fields.description",
				"Guardar traducciones");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.indicator.label_value", "Valor");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.indicator.label_value", "Value");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.indicator.label_value", "Valor");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.indicator.label_text", "Texto");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.indicator.label_text", "Text");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.indicator.label_text", "Texto");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.button_add_field", "Adicionar Campo");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.button_add_field", "Add Tag");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.button_add_field", "Agregar Campo");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.error.existing_tag",
				"Já existe um Campo com esta tag.");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.error.existing_tag",
				"Tag already exists.");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.error.existing_tag", "Campo ya existe.");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.error.existing_subfield",
				"Já existe um Subcampo com esta tag.");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.error.existing_subfield",
				"Subfield already exists.");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.error.existing_subfield",
				"Subcampo ya existe.");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.confirm_delete_datafield_title",
				"Excluir Campo");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.confirm_delete_datafield_title",
				"Delete Datafield");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.confirm_delete_datafield_title",
				"Excluir Campo");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.confirm_delete_datafield_description",
				"Você realmente deseja excluir este campo? Esta operação é irreversível, e o " +
				"campo só será apresentado na aba Marc.");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.confirm_delete_datafield_description",
				"Do you really wish to delete this datafield? This operation cannot be undone, " +
				"and the field will be displayed only on Marc tab.");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.confirm_delete_datafield_description",
				"¿Usted realmente desea excluir este campo? Esta operación es irreversible, y el " +
				"campo sólo se mostrará en la pestaña Marc.");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.form_customization.error.invalid_tag",
				"Campo Marc inválido. O campo Marc deve ser numérico, e possuir 3 digitos.");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.form_customization.error.invalid_tag",
				"Invalid Datafield Tag. The datafield Tag should be a 3 digits number.");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.form_customization.error.invalid_tag",
				"Campo Marc inválido. El campo Marc debe ser numérico con 3 dígitos.");
	}

	@Override
	public void doUpdateScopedBySchema(Connection connection) throws SQLException {
		for (RecordType recordType : RecordType.values()) {
			_addDatafieldSortOrderColumns(connection, recordType);

			_addSubfieldSortOrderColumns(connection, recordType);
		}
	}

	@Override
	public String getVersion() {
		return "5.0.0";
	}

	private void _addDatafieldSortOrderColumns(Connection connection, RecordType recordType)
		throws SQLException {

		String tableName = recordType + "_form_datafields";

		if (UpdateService.checkColumnExistence(tableName, "sort_order", connection)) {
			return;
		}

		_addSortOrderColumnForTable(tableName, connection);

		_updateSortOrderForTable(tableName, connection);
	}

	private void _addSubfieldSortOrderColumns(Connection connection, RecordType recordType)
		throws SQLException {

		String tableName = recordType + "_form_subfields";

		if (UpdateService.checkColumnExistence(tableName, "sort_order", connection)) {
			return;
		}

		_addSortOrderColumnForTable(tableName, connection);

		_updateSortOrderForTableWithSubfield(tableName, connection);
	}

	private void _addSortOrderColumnForTable(String tableName, Connection con) throws SQLException {
		StringBuilder addSortOrderColumnSQL =
				new StringBuilder(3)
					.append("ALTER TABLE ")
					.append(tableName)
					.append(" ADD COLUMN sort_order integer;");

		try (Statement addDatafieldColumnSt = con.createStatement()) {
			addDatafieldColumnSt.execute(addSortOrderColumnSQL.toString());
		}
	}

	private void _updateSortOrderForTable(String tableName, Connection con) throws SQLException {
		StringBuilder updateSql =
				new StringBuilder(3)
					.append("UPDATE ")
					.append(tableName)
					.append(" SET sort_order = (CAST(datafield as INT));");

		try (Statement updateSt = con.createStatement()) {
			updateSt.execute(updateSql.toString());
		}
	}

	private void _updateSortOrderForTableWithSubfield(
			String tableName, Connection con)
		throws SQLException {

		StringBuilder updateSql =
				new StringBuilder(3)
					.append("UPDATE ")
					.append(tableName)
					.append(" SET sort_order = (CAST(datafield as INT) + ASCII(subfield));");

		try (Statement updateSt = con.createStatement()) {
			updateSt.execute(updateSql.toString());
		}
	}
}
