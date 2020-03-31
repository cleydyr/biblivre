/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 * 
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou 
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como 
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da 
 * Licença, ou (caso queira) qualquer versão posterior.
 * 
 * Este programa é distribuído na esperança de que possa ser  útil, 
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 * 
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre.administration.accesscards;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import biblivre.administration.accesscards.validations.EndValidation;
import biblivre.administration.accesscards.validations.NumericEndValidation;
import biblivre.administration.accesscards.validations.NumericStartValidation;
import biblivre.administration.accesscards.validations.RequiredFieldValidation;
import biblivre.administration.accesscards.validations.StartEndOrderValidation;
import biblivre.administration.accesscards.validations.StartValidation;
import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.Validation;
import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;

public class Validator extends AbstractValidator {
	private static final String SAVE_ACTION = "save";

	@SuppressWarnings("serial")
	private static HashMap<String, Set<Validation>> validations = new HashMap<String, Set<Validation>>() {
		{
			put(SAVE_ACTION, new HashSet<Validation>(
				Arrays.asList(new Validation[] {
					new RequiredFieldValidation(),
					new EndValidation(),
					new StartValidation(),
					new NumericEndValidation(),
					new NumericStartValidation(),
					new StartEndOrderValidation(),
				})
			));
		}
	};

	public void validateSave(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		ValidationException ex = new ValidationException("error.form_invalid_values");

		for (Validation v : validations.get(SAVE_ACTION)) {
			v.validate(request, response, ex);
		}

		if (ex.hasErrors()) {
			handler.setMessage(ex);
		}
	}	
	
	public void validateDelete(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		Integer id = request.getInteger("id");
		if (id == 0) {
			handler.setMessage(ActionResult.WARNING, "administration.accesscards.error.card_not_found");
			return;
		}
	}
	
	public void validateBlockCard(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		Integer id = request.getInteger("id");
		if (id == 0) {
			handler.setMessage(ActionResult.WARNING, "administration.accesscards.error.card_not_found");
			return;
		}
	}
	
	public void validateUnblockCard(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		Integer id = request.getInteger("id");
		if (id == 0) {
			handler.setMessage(ActionResult.WARNING, "administration.accesscards.error.card_not_found");
			return;
		}
	}
	

}
