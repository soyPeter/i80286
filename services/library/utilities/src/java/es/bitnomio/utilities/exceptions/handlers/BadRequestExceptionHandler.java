package es.bitnomio.utilities.exceptions.handlers;

import es.bitnomio.utilities.exceptions.dtos.BadRequestExceptionBody;
import es.bitnomio.utilities.exceptions.dtos.HttpRestExceptionBody;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.validation.exceptions.ConstraintExceptionHandler;
import org.slf4j.Logger;

import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Singleton
@Produces
@Requires(classes = ConstraintViolationException.class)
@Replaces(ConstraintExceptionHandler.class)
class BadRequestExceptionHandler implements ExceptionHandler<ConstraintViolationException, MutableHttpResponse<?>> {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BadRequestExceptionHandler.class);

    @Override public MutableHttpResponse<?> handle(HttpRequest request, ConstraintViolationException exception) {

        var errors = exception.getConstraintViolations()
                .stream()
                .collect(Collectors.groupingBy(constraintViolation -> {

                            var path = constraintViolation.getPropertyPath().toString();
                            String property;
                            try {
                                property = path.substring(path.lastIndexOf(".") + 1);
                            }
                            catch (Exception e) {
                                log.warn("Path exception on {}", path);
                                property = path;
                            }
                            return property;
                        },
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toSet())));

        return HttpResponse.badRequest(new HttpRestExceptionBody(BadRequestExceptionBody.CODE, BadRequestExceptionBody.MESSAGE, errors));
    }
}
