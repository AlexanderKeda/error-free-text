package org.keda.errorfreetext.rest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.keda.errorfreetext.rest.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalRestExceptionHandler {

    private static final int DESERIALIZATION_ERROR = 40001;
    private static final int VALIDATION_ERROR = 40002;
    private static final int TASK_NOT_FOUND_ERROR = 40401;
    private static final int SERVER_INTERNAL_ERROR = 50000;

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNoSuchElementException(NoSuchElementException ex, HttpServletRequest request) {
        var error = new ErrorResponseDto(ex.getMessage(), TASK_NOT_FOUND_ERROR, Instant.now(), request.getRequestURI());
        log.warn("Handled NoSuchElementException: {}.",
                error.errorMessage());
        return error;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        var error = new ErrorResponseDto("Invalid argument provided", VALIDATION_ERROR, Instant.now(), request.getRequestURI());
        log.warn("Handled IllegalArgumentException: {}.",
                ex.getMessage());
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        var error = new ErrorResponseDto(message, VALIDATION_ERROR, Instant.now(), request.getRequestURI());
        log.warn("Handled MethodArgumentNotValidException: {}.",
                error.errorMessage());
        return error;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        var error = new ErrorResponseDto("Invalid request", DESERIALIZATION_ERROR, Instant.now(), request.getRequestURI());
        log.warn("Handled MethodArgumentTypeMismatchException: {}.",
                ex.getMessage());
        return error;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        var error = new ErrorResponseDto("Invalid request", DESERIALIZATION_ERROR, Instant.now(), request.getRequestURI());
        log.warn("Handled HttpMessageNotReadableException: {}.",
                ex.getMessage());
        return error;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleOtherExceptions(Exception ex, HttpServletRequest request) {
        var error = new ErrorResponseDto("Internal server error", SERVER_INTERNAL_ERROR, Instant.now(), request.getRequestURI());
        log.error("Unhandled error! {}: {}.",
                error.getClass().getSimpleName(),
                error.errorMessage(), ex);
        return error;
    }
}
