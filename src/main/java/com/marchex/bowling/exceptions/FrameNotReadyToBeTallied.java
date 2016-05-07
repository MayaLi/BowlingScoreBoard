package com.marchex.bowling.exceptions;

public class FrameNotReadyToBeTallied extends Exception {
    public FrameNotReadyToBeTallied() {
    }

    public FrameNotReadyToBeTallied(final String message) {
        super(message);
    }

    public FrameNotReadyToBeTallied(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FrameNotReadyToBeTallied(final Throwable cause) {
        super(cause);
    }

    public FrameNotReadyToBeTallied(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
