package com.starwars.backend.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Centralised error handler for all controllers.
 * - Every error returns the *same* JSON structure so the frontend always knows what to expect.
 * - 4xx client errors are logged as warnings (warn), 5xx as errors (error + stacktrace).
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Catch Spring exceptions that already carry an HTTP status.
    @ExceptionHandler({ResponseStatusException.class, ErrorResponseException.class})
    public ResponseEntity<ErrorResponse> handleStatusExceptions(Exception ex, HttpServletRequest req) {
        HttpStatusCode status;
        String message;

        if (ex instanceof ResponseStatusException rse) {
            status = rse.getStatusCode();
            // Sometimes reason is null, so fallback to the exception message
            message = firstNonBlank(rse.getReason(), rse.getMessage());
        } else {
            ErrorResponseException ere = (ErrorResponseException) ex;
            status = ere.getStatusCode();
            String detail = ere.getBody().getDetail();
            message = firstNonBlank(detail, ere.getMessage());
        }

        // Log with right level (warn for 4xx, error for 5xx)
        logByStatus(status, ex, req, message);
        return ResponseEntity.status(status).body(base(status, message, req).build());
    }

    // Handles 405 Method Not Allowed (e.g. POST to a GET endpoint)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
                                                                HttpServletRequest req) {
        HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;

        // Provide which methods are supported in the response, for easier debugging
        String supported = (ex.getSupportedMethods() != null && ex.getSupportedMethods().length > 0)
                ? String.join(", ", ex.getSupportedMethods())
                : "n/a";
        String message = String.format("Method %s is not allowed. Supported: %s.",
                ex.getMethod(), supported);

        log.warn("{} {} -> 405 {}", req.getMethod(), req.getRequestURI(), message);
        return ResponseEntity.status(status).body(base(status, message, req).build());
    }

    // Handles 404 Not Found (when enabled via properties)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex,
                                                          HttpServletRequest req) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = firstNonBlank(ex.getMessage(), "Resource not found");
        log.warn("{} {} -> 404 {}", req.getMethod(), req.getRequestURI(), message);
        return ResponseEntity.status(status).body(base(status, message, req).build());
    }

    // Handles bean validation errors (e.g. @Valid with @NotNull/@Size)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest req) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Flatten all field errors to a single readable message string
        List<String> fieldMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(this::prettyFieldError)
                .toList();

        String message = fieldMessages.isEmpty()
                ? "Validation failed"
                : "Validation failed: " + String.join("; ", fieldMessages);

        log.warn("{} {} -> 400 Validation error: {}", req.getMethod(), req.getRequestURI(), message);
        return ResponseEntity.status(status).body(base(status, message, req).build());
    }

    // Handles 400 Bad Request when a param is of the wrong type (e.g. page=abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest req) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String param    = ex.getName();
        String required = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "unknown";
        String message  = String.format("Parameter '%s' must be of type %s.", param, required);

        log.warn("{} {} -> 400 Type mismatch: {}", req.getMethod(), req.getRequestURI(), message);
        return ResponseEntity.status(status).body(base(status, message, req).build());
    }

    // Handles 400 Bad Request when a required param is missing (?page missing, etc.)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex,
                                                            HttpServletRequest req) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = String.format("Missing required parameter '%s' of type %s.",
                ex.getParameterName(), ex.getParameterType());
        log.warn("{} {} -> 400 {}", req.getMethod(), req.getRequestURI(), message);
        return ResponseEntity.status(status).body(base(status, message, req).build());
    }

    // Handles any unexpected errors (last resort)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // Full stacktrace is logged, but the response stays generic (never leak internals)
        log.error("{} {} -> 500 Unexpected error: {}", req.getMethod(), req.getRequestURI(),
                ex.getMessage(), ex);

        String message = "Unexpected error. Please try again later.";
        return ResponseEntity.status(status).body(base(status, message, req).build());
    }

    // ----------- Helpers below are just for internal usage -------------

    // Constructs the base error shape for all error responses
    private ErrorResponse.ErrorResponseBuilder base(HttpStatusCode status,
                                                    String message,
                                                    HttpServletRequest req) {
        return ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(reasonPhrase(status))
                .message(message)
                .path(req.getRequestURI());
    }

    // Returns the reason phrase for the status, e.g. "Not Found"
    private static String reasonPhrase(HttpStatusCode status) {
        return (status instanceof HttpStatus hs) ? hs.getReasonPhrase() : status.toString();
    }

    // Helper to get first non-blank string
    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) return a;
        if (b != null && !b.isBlank()) return b;
        return "Request failed";
    }

    // Logs with right severity based on status code
    private static void logByStatus(HttpStatusCode status, Exception ex,
                                    HttpServletRequest req, String message) {
        int code = status.value();
        if (code >= 500) {
            log.error("{} {} -> {} {}", req.getMethod(), req.getRequestURI(), code, message, ex);
        } else {
            log.warn("{} {} -> {} {}", req.getMethod(), req.getRequestURI(), code, message);
        }
    }

    // Formats a bean validation error like: "field: must not be blank"
    private String prettyFieldError(FieldError fe) {
        String msg = (fe.getDefaultMessage() == null || fe.getDefaultMessage().isBlank())
                ? "invalid value"
                : fe.getDefaultMessage();
        return fe.getField() + ": " + msg;
    }
}
