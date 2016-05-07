package com.marchex.bowling.exceptions;

public class FrameIsFullException extends Exception {
    public FrameIsFullException(final String message) {
        super(message);
    }
}
