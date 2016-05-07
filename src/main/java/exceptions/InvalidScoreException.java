package exceptions;

public class InvalidScoreException extends Exception {

    public InvalidScoreException() {
    }

    public InvalidScoreException(final String message) {
        super(message);
    }

    public InvalidScoreException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidScoreException(final Throwable cause) {
        super(cause);
    }

    public InvalidScoreException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
