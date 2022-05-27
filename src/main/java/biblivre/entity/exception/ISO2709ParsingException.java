package biblivre.entity.exception;

@SuppressWarnings("serial")
public class ISO2709ParsingException extends Exception {

    public ISO2709ParsingException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
