package biblivre.core;

import biblivre.core.exceptions.ValidationException;

public interface Validation {

	void validate(ExtendedRequest request, ExtendedResponse response, ValidationException e);

}
