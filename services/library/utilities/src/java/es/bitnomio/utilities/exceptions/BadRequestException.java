package es.bitnomio.utilities.exceptions;

import io.micronaut.http.HttpStatus;

public class BadRequestException extends RestRuntimeException {

    private static final String CODE = "BAD_REQUEST_EXCEPTION";
    private static final String MESSAGE = "Request payload could not be validated";

    private BadRequestException(String code, String message) {
        super(code, message, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message) {
        this(CODE, message);
    }

    public BadRequestException() {
        this(CODE, MESSAGE);
    }


}
