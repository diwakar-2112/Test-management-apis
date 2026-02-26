package com.testPortal.test_management_api.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {

    // The timestamp when the error occurred.
    private LocalDateTime timestamp;

    // The HTTP status code (e.g., 404).
    private int status;

    // The HTTP reason phrase (e.g., "Not Found").
    private String error;

    // A more detailed, developer-friendly message explaining the error.
    private String message;

    // The API path where the error happened.
    private String path;
}