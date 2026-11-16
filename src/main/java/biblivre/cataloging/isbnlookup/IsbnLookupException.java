package biblivre.cataloging.isbnlookup;

public class IsbnLookupException extends RuntimeException {
    public enum Reason {
        DISABLED,
        INVALID_ISBN,
        NOT_FOUND,
        UPSTREAM_ERROR
    }

    private final Reason reason;

    public IsbnLookupException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public IsbnLookupException(Reason reason, String message, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
