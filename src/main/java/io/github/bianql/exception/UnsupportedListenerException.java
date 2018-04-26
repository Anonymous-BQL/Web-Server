package io.github.bianql.exception;

public class UnsupportedListenerException extends RuntimeException {
    public UnsupportedListenerException(String message) {
        super(message);
    }

    public UnsupportedListenerException(String message, Throwable cause) {
        super(message, cause);
    }
}
