package es.bitnomio.utilities.exceptions.handlers;

import es.bitnomio.utilities.exceptions.RestRuntimeException;
import es.bitnomio.utilities.exceptions.dtos.HttpRestExceptionBody;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {RestRuntimeException.class, ExceptionHandler.class})
@Named("ServiceRestRuntimeExceptionHandler")
class RestExceptionHandler
    implements ExceptionHandler<RestRuntimeException, MutableHttpResponse<?>> {

    @Override
    public MutableHttpResponse<?> handle(HttpRequest request, RestRuntimeException exception) {
        HttpRestExceptionBody exceptionBody = new HttpRestExceptionBody(
            exception.getCode(),
            exception.getMessage()
        );

        if (exception.getHttpStatusCode() != null) {
            return HttpResponse.status(exception.getHttpStatusCode()).body(exceptionBody);
        }
        else {
            return HttpResponse.unprocessableEntity().body(exceptionBody);
        }
    }

}
