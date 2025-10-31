package com.event.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String method;
    private Map<String, String> fieldErrors;

    public static String currentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}

