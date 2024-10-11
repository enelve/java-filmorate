package ru.yandex.practicum.filmorate.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ExceptionHandlerExceptionResolver {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler({NotFoundException.class, EmptyResultDataAccessException.class})
    public ResponseEntity<ErrorResponse> handleException(NotFoundException e) {
        log.error(e.getMessage());
        ErrorResponse response = new ErrorResponse(NOT_FOUND.value(), e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler({NotFoundErrorException.class})
    public ResponseEntity<ErrorResponseNew> handleException(NotFoundErrorException e) {
        log.error(e.getMessage());
        ErrorResponseNew response = new ErrorResponseNew(NOT_FOUND.value(), e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> handleException(DuplicateException e) {
        log.error(e.getMessage());
        ErrorResponse response = new ErrorResponse(BAD_REQUEST.value(), e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(NotContentException.class)
    public ResponseEntity<ErrorResponse> handleException(NotContentException e) {
        log.error(e.getMessage());
        ErrorResponse response = new ErrorResponse(NO_CONTENT.value(), e.getMessage(), LocalDateTime.now());
        return ResponseEntity
                .status(NO_CONTENT)
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<String>> handleException(ConstraintViolationException e) {
        final List<String> errorList = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        log.error("onConstraintViolationException. {}", errorList);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorList);
    }
}
