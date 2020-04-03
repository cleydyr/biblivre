package biblivre.administration.accesscards.validations;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.exceptions.ValidationException;

public class RequiredFieldValidation extends AbstractAccessCardFieldValidation {
	@Override
	public void validate(ExtendedRequest request, ExtendedResponse response, ValidationException e) {
		String code = request.getString("code");

		boolean single = StringUtils.isNotBlank(code);

		boolean multiple = isMultiple(request);

		if (!single && !multiple) {
			e.addError("code", "field.error.required");
		}
	}
}
