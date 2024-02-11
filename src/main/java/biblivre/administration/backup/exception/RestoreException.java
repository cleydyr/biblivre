package biblivre.administration.backup.exception;

@SuppressWarnings("serial")
public class RestoreException extends Exception {
    public RestoreException(Throwable cause) {
        super(cause);
    }

    public RestoreException(String message) {
        super(message);
    }
}
