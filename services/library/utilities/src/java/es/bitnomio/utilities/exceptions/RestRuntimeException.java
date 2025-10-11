package es.bitnomio.utilities.exceptions;

import io.micronaut.http.HttpStatus;

public class RestRuntimeException extends RuntimeException {
    private String code;
    private String message;
    private HttpStatus httpStatusCode;

    public RestRuntimeException(String code, String message, HttpStatus httpStatusCode) {
        super(message);
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    public RestRuntimeException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(HttpStatus httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    @Override
    public String toString() {
        return (
                "RestRuntimeException{" +
                        "code='" +
                        code +
                        '\'' +
                        ", message='" +
                        message +
                        '\'' +
                        ", httpStatusCode='" +
                        httpStatusCode +
                        '\'' +
                        '}'
        );
    }
}
