package biblivre.administration.accesscards.validations;

import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.exceptions.ValidationException;

public class StartEndOrderValidation extends AbstractAccessCardFieldValidation {

	@Override
	public void validate(ExtendedRequest request, ExtendedResponse response, ValidationException e) {
		Integer startInt = request.getInteger("start", null);
		Integer endInt = request.getInteger("end", null);

		if (startInt != null && endInt != null && startInt >= endInt) {
			e.addError("start", "administration.accesscards.error.start_less_than_or_equals_end");
			e.addError("end", "administration.accesscards.error.start_less_than_or_equals_end");
		}
	}

}
