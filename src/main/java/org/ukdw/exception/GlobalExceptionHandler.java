package org.ukdw.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.ukdw.dto.response.ErrorResponse;

import java.time.Instant;

@RestControllerAdvice
public final class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Resource Not Found",
                ex.getMessage(),
                Instant.now().toString()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RequestParameterErrorException.class)
    public ResponseEntity<ErrorResponse> handleRequestParameterErrorException(RequestParameterErrorException ex) {
        // Create an error response object with the exception message
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Request Parameter Error",
                ex.getMessage(),
                Instant.now().toString()
        );

        // Return the error response with a BAD_REQUEST status
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        // Create an error response object with the exception message
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                ex.getMessage(),
                Instant.now().toString()
        );

        // Return the error response with a BAD_REQUEST status
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Optionally handle other exceptions (REMOVE IF YOU WANT TO DEBUG PROBLEM, THIS HANDLER WILL HIDE ERRORS)
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
//        ErrorResponse errorResponse = new ErrorResponse(
//                ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString(),
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                "An unexpected error occurred",
//                ex.getMessage(),
//                Instant.now().toString()
//        );
//        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Authorization Denied",
                ex.getMessage(),
                Instant.now().toString()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}
