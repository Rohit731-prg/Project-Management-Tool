package com.example.server.Exception;

public class SingleMessageException extends RuntimeException {
    public SingleMessageException(String message) {
        super(message);
    }
}
