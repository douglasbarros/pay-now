package com.ezycollect.server.entrypoint.rest;

import com.ezycollect.server.domain.exception.PaymentNotFoundException;
import com.ezycollect.server.domain.exception.PaymentProcessingException;
import com.ezycollect.server.domain.exception.PaymentValidationException;
import com.ezycollect.server.domain.exception.WebhookNotFoundException;
import com.ezycollect.server.domain.exception.WebhookValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler for REST controllers. Maps domain exceptions to appropriate HTTP
 * responses following Hexagonal Architecture principles.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

        /**
         * Handles Payment Not Found exceptions.
         * 
         * @return 404 Not Found
         */
        @ExceptionHandler(PaymentNotFoundException.class)
        public ResponseEntity<ErrorResponse> handlePaymentNotFoundException(
                        PaymentNotFoundException ex, HttpServletRequest request) {

                ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                                "Payment Not Found", ex.getMessage(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        /**
         * Handles Payment Validation exceptions.
         * 
         * @return 422 Unprocessable Entity
         */
        @ExceptionHandler(PaymentValidationException.class)
        public ResponseEntity<ErrorResponse> handlePaymentValidationException(
                        PaymentValidationException ex, HttpServletRequest request) {

                ErrorResponse error = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                "Payment Validation Error", ex.getMessage(),
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
        }

        /**
         * Handles Payment Processing exceptions.
         * 
         * @return 422 Unprocessable Entity
         */
        @ExceptionHandler(PaymentProcessingException.class)
        public ResponseEntity<ErrorResponse> handlePaymentProcessingException(
                        PaymentProcessingException ex, HttpServletRequest request) {

                ErrorResponse error = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                "Payment Processing Error", ex.getMessage(),
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
        }

        /**
         * Handles Webhook Not Found exceptions.
         * 
         * @return 404 Not Found
         */
        @ExceptionHandler(WebhookNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleWebhookNotFoundException(
                        WebhookNotFoundException ex, HttpServletRequest request) {

                ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                                "Webhook Not Found", ex.getMessage(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        /**
         * Handles Webhook Validation exceptions.
         * 
         * @return 422 Unprocessable Entity
         */
        @ExceptionHandler(WebhookValidationException.class)
        public ResponseEntity<ErrorResponse> handleWebhookValidationException(
                        WebhookValidationException ex, HttpServletRequest request) {

                ErrorResponse error = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                "Webhook Validation Error", ex.getMessage(),
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {

                String message = ex.getBindingResult().getFieldErrors().stream()
                                .map(FieldError::getDefaultMessage)
                                .collect(Collectors.joining(", "));

                ErrorResponse error = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                "Validation Error", message, request.getRequestURI());

                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(Exception ex,
                        HttpServletRequest request) {

                ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Internal Server Error", "An unexpected error occurred",
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
}
