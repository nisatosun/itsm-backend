package com.nisa.itsm.exception.handler;

import com.nisa.itsm.common.dto.ErrorResponse;
import com.nisa.itsm.exception.custom.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔴 VALIDATION ERROR
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        return new ErrorResponse(
                400,
                "Validation Error",
                "Request validation failed",
                request.getRequestURI(),
                details
        );
    }

    // 🟠 BUSINESS ERROR (CUSTOM)
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExistsException(
            UserAlreadyExistsException ex,
            HttpServletRequest request) {

        return new ErrorResponse(
                409,
                "Conflict",
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );
    }

    // ⚫ GENERIC ERROR
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        return new ErrorResponse(
                500,
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException ex,
            HttpServletRequest request) {

        return new ErrorResponse(
                403,
                "Forbidden",
                "Access denied",
                request.getRequestURI(),
                List.of()
        );
    }
}
