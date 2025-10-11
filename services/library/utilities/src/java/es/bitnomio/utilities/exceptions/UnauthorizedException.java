package es.bitnomio.utilities.exceptions;

import io.micronaut.http.HttpStatus;

public class UnauthorizedException extends RestRuntimeException {

    private static final String CODE = "UNAUTHORIZED_EXCEPTION";
    private static final String MESSAGE = "There was a problem with user authorization or token is expired";

    public UnauthorizedException(String code, String message, HttpStatus httpStatusCode) {
        super(code, message, httpStatusCode);
    }

    public UnauthorizedException(String code) {
        super(code, MESSAGE, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(String code, String message) {
        super(code, MESSAGE, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException() {
        super(CODE, MESSAGE, HttpStatus.UNAUTHORIZED);
    }

}
