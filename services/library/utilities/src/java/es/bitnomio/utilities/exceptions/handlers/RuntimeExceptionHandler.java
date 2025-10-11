package es.bitnomio.utilities.exceptions.handlers;

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
@Requires(classes = {RuntimeException.class, ExceptionHandler.class})
@Named("ServiceRestExceptionHandler")
class RuntimeExceptionHandler implements ExceptionHandler<RuntimeException, MutableHttpResponse<?>> {

    @Override
    public MutableHttpResponse<?> handle(HttpRequest request, RuntimeException exception) {

        HttpRestExceptionBody exceptionBody = new HttpRestExceptionBody(
            HttpResponse.serverError().toString(),
            exception.getMessage()
        );

        return HttpResponse.serverError().body(exceptionBody);

    }

}
