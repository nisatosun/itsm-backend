package com.nisa.itsm.exception.handler;

import com.nisa.itsm.common.dto.ErrorResponse;
import com.nisa.itsm.common.exception.ResourceNotFoundException;
import com.nisa.itsm.exception.custom.BadRequestException;
import com.nisa.itsm.exception.custom.ConflictException;
import com.nisa.itsm.exception.custom.CustomAccessDeniedException;
import com.nisa.itsm.exception.custom.UserAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.List;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
                                details);
        }

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
                                List.of());
        }

        @ExceptionHandler(ConflictException.class)
        @ResponseStatus(HttpStatus.CONFLICT)
        public ErrorResponse handleConflictException(
                        ConflictException ex,
                        HttpServletRequest request) {

                return new ErrorResponse(
                                409,
                                "Conflict",
                                ex.getMessage(),
                                request.getRequestURI(),
                                List.of());
        }

        @ExceptionHandler(BadRequestException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleBadRequestException(
                        BadRequestException ex,
                        HttpServletRequest request) {

                return new ErrorResponse(
                                400,
                                "Bad Request",
                                ex.getMessage(),
                                request.getRequestURI(),
                                List.of());
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ErrorResponse handleResourceNotFoundException(
                        ResourceNotFoundException ex,
                        HttpServletRequest request) {

                return new ErrorResponse(
                                404,
                                "Not Found",
                                ex.getMessage(),
                                request.getRequestURI(),
                                List.of());
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
                                List.of());
        }

        @ExceptionHandler(CustomAccessDeniedException.class)
        @ResponseStatus(HttpStatus.FORBIDDEN)
        public ErrorResponse handleCustomAccessDeniedException(
                        CustomAccessDeniedException ex,
                        HttpServletRequest request) {

                return new ErrorResponse(
                                403,
                                "Forbidden",
                                ex.getMessage(),
                                request.getRequestURI(),
                                List.of());
        }

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
                                List.of());
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
                IllegalArgumentException ex,
                HttpServletRequest request
        ) {
                ErrorResponse errorResponse = new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        List.of()
                );

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
}
