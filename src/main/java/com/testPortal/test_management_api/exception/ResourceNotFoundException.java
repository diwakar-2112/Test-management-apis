package com.testPortal.test_management_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// This annotation tells Spring to respond with a 404 NOT FOUND status
// whenever this exception is thrown and not caught by a specific handler.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // A standard constructor that takes a message.
    public ResourceNotFoundException(String message) {
        super(message);
    }
}