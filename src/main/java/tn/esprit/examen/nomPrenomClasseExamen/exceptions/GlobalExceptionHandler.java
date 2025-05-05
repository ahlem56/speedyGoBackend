package tn.esprit.examen.nomPrenomClasseExamen.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tn.esprit.examen.nomPrenomClasseExamen.exceptions.ErrorResponse;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : "Unknown";
                    String errorMessage = error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value";
                    return fieldName + ": " + errorMessage;
                })
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(new ErrorResponse("Validation failed: " + errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        return new ResponseEntity<>(new ErrorResponse("Internal server error: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}