package es.bitnomio.utilities.exceptions.handlers;

import es.bitnomio.utilities.exceptions.dtos.HttpRestExceptionBody;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.convert.exceptions.ConversionErrorException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.exceptions.ConversionErrorHandler;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Replaces(ConversionErrorHandler.class)
@Named("ServiceConversionErrorExceptionHandler")
@Singleton
class ConversionErrorExceptionHandler
    implements ExceptionHandler<ConversionErrorException, HttpResponse<?>> {

    @Override
    public HttpResponse<?> handle(HttpRequest request, ConversionErrorException exception) {

        return HttpResponse.badRequest(new HttpRestExceptionBody("DATA_CONVERSION_EXCEPTION",
            exception.getMessage()));
    }
}
