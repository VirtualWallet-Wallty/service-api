package com.krushkov.virtualwallet.exceptions;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import com.krushkov.virtualwallet.models.dtos.responses.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityDuplicateException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicate(
            EntityDuplicateException e,
            HttpServletRequest request
    ) {
        return error(HttpStatus.CONFLICT, request, e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            EntityNotFoundException e,
            HttpServletRequest request
    ) {
        return error(HttpStatus.NOT_FOUND, request, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error(
                request.getRequestURI(),
                ConstantMessages.VALIDATION_ERROR,
                errors));
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidOperation(
            InvalidOperationException e,
            HttpServletRequest request
    ) {
        return error(HttpStatus.BAD_REQUEST, request, e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(HttpServletRequest request) {
        return error(HttpStatus.UNAUTHORIZED, request, ConstantMessages.WRONG_CREDENTIALS_ERROR);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleWrongUri(HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, request, ConstantMessages.WRONG_ENDPOINT_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(HttpServletRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, request, ConstantMessages.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiResponse<Void>> error(HttpStatus status, HttpServletRequest request, String message) {
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(
                        request.getRequestURI(),
                        message,
                        null
                ));
    }
}
