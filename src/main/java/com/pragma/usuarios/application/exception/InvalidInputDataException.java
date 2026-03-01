package com.pragma.usuarios.application.exception;

import java.util.Map;

public class InvalidInputDataException extends ApplicationException {

    public InvalidInputDataException(String field, String reason) {
        super("INVALID_INPUT",
                String.format("Invalid input for field '%s': %s", field, reason),
                Map.of("field", field, "reason", reason));
    }
}
