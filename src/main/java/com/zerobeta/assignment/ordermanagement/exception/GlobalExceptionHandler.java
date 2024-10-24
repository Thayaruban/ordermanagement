package com.zerobeta.assignment.ordermanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDetails> handleRuntimeException(RuntimeException ex, WebRequest request) {
        return buildResponseEntity(ex.getMessage(), request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(EntityNotFoundException ex, WebRequest request) {
        return buildResponseEntity(ex.getMessage(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleUserAlreadyExists(UserAlreadyExistsException ex, WebRequest request) {
        return buildResponseEntity(ex.getMessage(), request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorDetails> handleNoHandlerFoundException(NoResourceFoundException ex, WebRequest request) {
        String errorMessage = "The requested URL was not found on this server.";
        return buildResponseEntity(errorMessage, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDetails> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        String errorMessage = "Invalid Request Body. Please check the data types of your inputs.";
        return buildResponseEntity(errorMessage, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDetails> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        String errorMessage = "Request method '" + ex.getMethod() + "' is not supported for this endpoint.";
        ErrorDetails errorDetails = new ErrorDetails(errorMessage, request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        return buildResponseEntity(ex.getMessage(), request, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * Handles validation exceptions that occur when request parameters
     * fail validation checks (e.g., when using @Valid ,@NotBlank, @ValidPassword annotations).
     *
     * @param ex      The MethodArgumentNotValidException that contains
     *                details about the validation errors.
     * @param request The current web request, providing context for the error.
     * @return A ResponseEntity containing an ErrorDetails object with a
     *         concatenated string of validation error messages and a
     *         BAD REQUEST (400) HTTP status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        StringBuilder concatenatedErrors = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            concatenatedErrors.append(errorMessage).append("; ");
        });

        if (!concatenatedErrors.isEmpty()) {
            concatenatedErrors.setLength(concatenatedErrors.length() - 2); // Remove the last "; "
        }
        return buildResponseEntity(concatenatedErrors.toString(), request, HttpStatus.BAD_REQUEST);
    }



    private ResponseEntity<ErrorDetails> buildResponseEntity(String message, WebRequest request, HttpStatus status) {
        ErrorDetails errorDetails = new ErrorDetails(message, request.getDescription(false));
        return new ResponseEntity<>(errorDetails, status);
    }
}
