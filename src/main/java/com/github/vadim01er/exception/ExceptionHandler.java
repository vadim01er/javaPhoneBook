package com.github.vadim01er.exception;

import com.github.vadim01er.json.ExceptionResponse;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected @NonNull ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
                                                                           @NonNull HttpHeaders headers,
                                                                           @NonNull HttpStatus status,
                                                                           @NonNull WebRequest request) {
        return ResponseEntity.status(status).body(
                new ExceptionResponse(status, "Unformed JSON in body"));
    }


    @Override
    protected @NonNull ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                                    @NonNull HttpHeaders headers,
                                                                                    @NonNull HttpStatus status,
                                                                                    @NonNull WebRequest request) {
        return ResponseEntity.status(status).body(
                new ExceptionResponse(status,
                        "Invalid JSON with field: " + ex.getBindingResult().getFieldErrors().stream()
                                .map(FieldError::getField)
                                .collect(Collectors.toList())));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                      WebRequest request) {
        return ResponseEntity.badRequest().body(
                new ExceptionResponse(HttpStatus.BAD_REQUEST,
                        String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName())));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex,
                                                                      WebRequest request) {
        return ResponseEntity.badRequest().body(
                new ExceptionResponse(HttpStatus.BAD_REQUEST, "No valid parameter: " + ex.getMessage()));
    }

    @Override
    protected @NonNull ResponseEntity<Object> handleNoHandlerFoundException(@NonNull NoHandlerFoundException ex,
                                                                            @NonNull HttpHeaders headers,
                                                                            @NonNull HttpStatus status,
                                                                            @NonNull WebRequest request) {
        return ResponseEntity.status(status).body(
                new ExceptionResponse(status, "Handler not found"));
    }
}


