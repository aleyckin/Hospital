package com.example.hospital.Util.Validation;

import java.util.Set;

public class ValidationException extends RuntimeException {
    public ValidationException(Set<String> errors) {
        super(String.join("\n", errors));
    }

    public ValidationException(String message) {
        super(message);
    }
}