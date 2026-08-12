package biblivre.circulation.addresslookup;

public class AddressLookupException extends RuntimeException {
    public enum Reason {
        DISABLED,
        INVALID_CEP,
        NOT_FOUND,
        UPSTREAM_ERROR
    }

    private final Reason reason;

    public AddressLookupException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public AddressLookupException(Reason reason, String message, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
