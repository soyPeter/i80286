package es.bitnomio.utilities.exceptions.http;

import es.bitnomio.utilities.exceptions.RestRuntimeException;
import es.bitnomio.utilities.exceptions.dtos.HttpRestExceptionBody;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.Optional;

@Singleton
@Named("ServiceBitnomioExceptionJsonHandler")
class ExceptionJsonHandler<T>  {

    Class<T> exception;

    public ExceptionJsonHandler(Class<T> exception) {
        this.exception = exception;
    }

    public RestRuntimeException handle(HttpClientResponseException httpException) {

        String code = "unprocessable_entity";
        String message = "";
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        Optional<T> jsonError = httpException.getResponse().getBody(exception);

        if (jsonError.isPresent() &&
            (jsonError.get() instanceof HttpRestExceptionBody httpRestExceptionBody)) {
            code = httpRestExceptionBody.code();
            message = httpRestExceptionBody.message();
            status = HttpStatus.valueOf(httpException.getStatus().getCode());
        }

        return new RestRuntimeException(code, message, status);
    }
}

