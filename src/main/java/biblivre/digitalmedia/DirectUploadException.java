package biblivre.digitalmedia;

import java.io.Serial;

/** A direct upload step was refused; the message is a translation key for the user. */
public class DirectUploadException extends RuntimeException {
    @Serial private static final long serialVersionUID = 1L;

    public DirectUploadException(String messageKey) {
        super(messageKey);
    }
}
