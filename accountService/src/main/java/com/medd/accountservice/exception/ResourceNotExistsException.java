package com.medd.accountservice.exception;

public class ResourceNotExistsException extends RuntimeException {

    public ResourceNotExistsException() {
        super();
    }

    public ResourceNotExistsException(String message) {
        super(message);
    }

}

