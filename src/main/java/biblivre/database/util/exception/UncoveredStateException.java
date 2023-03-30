package biblivre.database.util.exception;

@SuppressWarnings("serial")
public class UncoveredStateException extends RuntimeException {

    public UncoveredStateException(String msg) {
        super(msg);
    }
}
