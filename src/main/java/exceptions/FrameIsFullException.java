package exceptions;

public class FrameIsFullException extends Exception {
    public FrameIsFullException() {
    }

    public FrameIsFullException(final String message) {
        super(message);
    }

    public FrameIsFullException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FrameIsFullException(final Throwable cause) {
        super(cause);
    }

    public FrameIsFullException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
