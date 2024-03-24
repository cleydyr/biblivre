package biblivre.administration.reports.v2.exception;

public class ReportException extends Exception {
    public ReportException(String message) {
        super(message);
    }

    public ReportException(String message, Throwable cause) {
        super(message, cause);
    }
}
