package biblivre.loggingfixture;

import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;

/**
 * Package and class name are load-bearing. {@code Controller} loads {@code
 * biblivre.<module>.Validator} by name, and this fixture is that class for module {@code
 * loggingfixture}.
 */
public class Validator extends AbstractValidator {

    public void validateExplode(
            AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
        throw new IllegalStateException("validator blew up");
    }
}
