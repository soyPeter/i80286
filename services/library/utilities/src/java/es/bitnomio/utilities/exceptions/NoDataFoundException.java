package es.bitnomio.utilities.exceptions;

import io.micronaut.http.HttpStatus;

public class NoDataFoundException extends RestRuntimeException {
    private static final String CODE = "NO_DATA_FOUND";
    private static final String MESSAGE = "No data found";

    public NoDataFoundException(String code, String message) {
        super(code, message, HttpStatus.NOT_FOUND);
    }

    public NoDataFoundException() {
        this(CODE, MESSAGE);
    }

    public NoDataFoundException(String message) {
        this(CODE, message);
    }


}
