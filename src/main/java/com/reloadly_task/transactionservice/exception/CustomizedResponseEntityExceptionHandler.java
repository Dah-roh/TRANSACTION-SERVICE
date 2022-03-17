package com.reloadly_task.transactionservice.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

    public CustomizedResponseEntityExceptionHandler() {
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex, final HttpHeaders headers,
            final HttpStatus status, final WebRequest request) {

        return buildResponseEntity(getValidationErrors(ex.getBindingResult()
                .getFieldErrors()), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException
            (final DataIntegrityViolationException de) {

        return buildResponseEntity("caught an exception while trying to process request",
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            final HttpMessageNotReadableException ex, final HttpHeaders headers,
            final HttpStatus status, final WebRequest request) {

        return buildResponseEntity("Malformed JSON request",
                HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(
            final MissingPathVariableException ex, final HttpHeaders headers,
            final HttpStatus status, final WebRequest request) {

        return handleExceptionInternal(ex, getError(ex), headers,
                status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            final MissingServletRequestParameterException ex,
            final HttpHeaders headers, final HttpStatus status,
            final WebRequest request) {

        return handleExceptionInternal(ex, getError(ex),
                headers, status, request);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(
            final CustomException ce) {

        return buildResponseEntity(ce.getMessage(), ce.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            final HttpMediaTypeNotSupportedException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request) {

        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(
                "media type is not supported. Supported media types are"
        );
        ex.getSupportedMediaTypes().forEach(t ->
                builder.append(t).append(", "));
        return buildResponseEntity(builder.substring(0, builder.length() - 2),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(
            final HttpMessageNotWritableException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request) {

        return buildResponseEntity("Error writing JSON output",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            final NoHandlerFoundException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request) {

        final String message =
                String.format("Could not find the %s method for URL %s",
                        ex.getHttpMethod(), ex.getRequestURL());
        return buildResponseEntity(message, HttpStatus.NOT_FOUND);
    }


    private ResponseEntity<Object> buildResponseEntity(
            final String apiResponse, final HttpStatus status) {

        return new ResponseEntity<>(getError(apiResponse), status);
    }

    private ResponseEntity<Object> buildResponseEntity(
            final Object apiResponse, final HttpStatus status) {

        return new ResponseEntity<>(apiResponse, status);
    }


    private Map<String, String> getValidationErrors(
            final List<FieldError> fieldErrors) {

        final Map<String, String> errors = new HashMap<>();
        fieldErrors.forEach(e -> errors.put(e.getField(),
                e.getDefaultMessage()));

        return errors;
    }

    private Map<String, Object> getError(final Exception ex) {
        final Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("status", false);
        response.put("timeStamp", ZonedDateTime.now());
        response.put("data", null);

        return response;
    }

    private Map<String, Object> getError(final String message) {
        final Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("status", false);
        response.put("timeStamp", ZonedDateTime.now());
        response.put("data", null);

        return response;
    }
}
