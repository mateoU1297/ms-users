package com.pragma.usuarios.infrastructure.exception;

import java.util.Map;

public class JwtTokenException extends InfrastructureException {

    public JwtTokenException(String message) {
        super("JWT_ERROR", message);
    }

    public JwtTokenException(String message, String token) {
        super("JWT_ERROR", message, Map.of("token", token));
    }
}
