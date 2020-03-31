package biblivre.administration.accesscards.validations;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.Validation;
import biblivre.core.exceptions.ValidationException;

public abstract class AbstractAccessCardFieldValidation implements Validation {

	public AbstractAccessCardFieldValidation() {
		super();
	}

	public abstract void validate(ExtendedRequest request, ExtendedResponse response, ValidationException e);

	protected boolean isMultiple(ExtendedRequest request) {
		String prefix = request.getString("prefix");
		String start = request.getString("start");
		String end = request.getString("end");
		String suffix = request.getString("suffix");

		boolean multiple = StringUtils.isNotBlank(start + end + prefix + suffix);
		return multiple;
	}

}
