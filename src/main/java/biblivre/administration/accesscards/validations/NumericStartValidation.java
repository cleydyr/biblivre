package biblivre.administration.accesscards.validations;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.exceptions.ValidationException;

public class NumericStartValidation extends AbstractAccessCardFieldValidation {

	@Override
	public void validate(ExtendedRequest request, ExtendedResponse response, ValidationException e) {
		if (!StringUtils.isNumeric(request.getString("start"))) {
			e.addError("start", "field.error.digits_only");
		}
	}

}
