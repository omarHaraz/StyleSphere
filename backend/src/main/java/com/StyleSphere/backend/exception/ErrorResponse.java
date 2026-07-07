package com.StyleSphere.backend.exception;

import java.time.LocalDateTime;


import java.time.LocalDateTime;

public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String path; // Presumably your 3rd field

    // 3-Argument Constructor
    public ErrorResponse(int status, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.path = path;
    }

    // 2-Argument Constructor (The one you are trying to use!)
    public ErrorResponse(int status, String message) {
        this(status, message, "N/A"); // Defaults the path to "N/A"
    }

    // ... getters
}