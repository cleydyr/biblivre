package biblivre.administration.backup;

public class BackupException extends Exception {
    public BackupException(String message) {
        super(message);
    }

    public BackupException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
