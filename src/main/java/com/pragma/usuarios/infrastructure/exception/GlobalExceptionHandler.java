package com.pragma.usuarios.infrastructure.exception;

import com.pragma.usuarios.application.dto.response.ErrorResponse;
import com.pragma.usuarios.application.exception.ApplicationException;
import com.pragma.usuarios.domain.exception.DomainException;
import com.pragma.usuarios.domain.exception.UserAlreadyExistsException;
import com.pragma.usuarios.domain.exception.UserUnderageException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(
            DomainException ex, HttpServletRequest request) {

        log.warn("Domain exception: {} - {}", ex.getErrorCode(), ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .details(ex.getDetails())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UserUnderageException.class)
    public ResponseEntity<ErrorResponse> handleUserUnderage(
            UserUnderageException ex, HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("USER_UNDERAGE")
                .message("El usuario debe ser mayor de edad")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .details(ex.getDetails())
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException ex, HttpServletRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("USER_ALREADY_EXISTS")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .details(ex.getDetails())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(
            ApplicationException ex, HttpServletRequest request) {

        log.error("Application exception: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(ex.getErrorCode())
                .message("Error processing request: " + ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .details(ex.getDetails())
                .build();

        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<ErrorResponse> handleInfrastructureException(
            InfrastructureException ex, HttpServletRequest request) {

        log.error("Infrastructure exception: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(ex.getErrorCode())
                .message("Technical error occurred. Please try again later.")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .details(ex.getDetails())
                .build();

        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        Map<String, Object> details = new HashMap<>();
        fieldErrors.forEach(error ->
                details.put(error.getField(), error.getDefaultMessage())
        );

        List<ErrorResponse.FieldValidationError> errors = fieldErrors.stream()
                .map(error -> ErrorResponse.FieldValidationError.builder()
                        .field(error.getField())
                        .rejectedValue(error.getRejectedValue() != null ?
                                error.getRejectedValue().toString() : null)
                        .message(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message("The request contains invalid fields")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .details(details)
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("INVALID_CREDENTIALS")
                .message("Invalid email or password")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error("ACCESS_DENIED")
                .message("You don't have permission to access this resource")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_ERROR")
                .message("An unexpected error occurred")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.internalServerError().body(errorResponse);
    }
}