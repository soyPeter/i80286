package es.bitnomio.utilities.exceptions.handlers;

import es.bitnomio.utilities.exceptions.dtos.BadRequestExceptionBody;
import es.bitnomio.utilities.exceptions.dtos.HttpRestExceptionBody;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.server.exceptions.UnsatisfiedRouteHandler;
import io.micronaut.web.router.exceptions.UnsatisfiedRouteException;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Replaces(UnsatisfiedRouteHandler.class)
@Named("ServiceUnsatisfiedRouteExceptionHandler")
@Singleton
class UnsatisfiedRouteExceptionHandler
    implements ExceptionHandler<UnsatisfiedRouteException, HttpResponse<?>> {

    @Override
    public HttpResponse<?> handle(HttpRequest request, UnsatisfiedRouteException exception) {

        return HttpResponse.badRequest(new HttpRestExceptionBody(BadRequestExceptionBody.CODE,
            exception.getMessage()));
    }
}
