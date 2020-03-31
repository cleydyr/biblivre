package biblivre.administration.accesscards.validations;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.Validation;
import biblivre.core.exceptions.ValidationException;

public class RequiredFieldValidation implements Validation {
	@Override
	public void validate(ExtendedRequest request, ExtendedResponse response, ValidationException e) {
		String code = request.getString("code");
		String prefix = request.getString("prefix");
		String start = request.getString("start");
		String end = request.getString("end");
		String suffix = request.getString("suffix");

		boolean single = StringUtils.isNotBlank(code);

		boolean multiple = StringUtils.isNotBlank(start) || StringUtils.isNotBlank(end) || StringUtils.isNotBlank(prefix) || StringUtils.isNotBlank(suffix);

		if (!single && !multiple) {
			e.addError("code", "field.error.required");
		}
	}
}
