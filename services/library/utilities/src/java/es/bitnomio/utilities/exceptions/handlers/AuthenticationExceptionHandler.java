package es.bitnomio.utilities.exceptions.handlers;

import es.bitnomio.utilities.exceptions.dtos.HttpRestExceptionBody;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.security.authentication.AuthenticationException;
import jakarta.inject.Singleton;

@Produces
@Requires(classes = AuthenticationException.class)
@Replaces(AuthenticationExceptionHandler.class)
@Singleton
class AuthenticationExceptionHandler implements ExceptionHandler<AuthenticationException, HttpResponse<?>> {

  private static final String CODE = "FORBIDDEN_EXCEPTION";

  private static final String MESSAGE = "There was a problem with credentials or token is expired";

  @Override
  public HttpResponse<?> handle(HttpRequest request, AuthenticationException exception) {
    HttpRestExceptionBody exceptionBody = new HttpRestExceptionBody(
        CODE,
        MESSAGE
    );

    return HttpResponse.status(HttpStatus.FORBIDDEN).body(exceptionBody);

  }

}
