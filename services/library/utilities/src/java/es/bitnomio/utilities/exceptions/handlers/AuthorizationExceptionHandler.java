/*
 * COPYRIGHT 2020 -2024 original authors
 * mailto:bitnomio-backend@bitnomio.es
 *
 * bitnomio-spectrum - Created by bitnomio@bitnomio
 * Date: 11/11/24 Time: 17:01
 *
 */
package es.bitnomio.utilities.exceptions.handlers;

import es.bitnomio.utilities.exceptions.dtos.HttpRestExceptionBody;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.security.authentication.AuthorizationException;
import io.micronaut.security.authentication.DefaultAuthorizationExceptionHandler;
import jakarta.inject.Singleton;

@Produces
@Requires(classes = AuthorizationException.class)
@Replaces(DefaultAuthorizationExceptionHandler.class)
@Singleton
class AuthorizationExceptionHandler implements ExceptionHandler<AuthorizationException, HttpResponse<?>> {

  private static final String CODE = "UNAUTHORIZED_EXCEPTION";
  private static final String MESSAGE = "There was a problem with user authorization or token is expired";

  @Override
  public HttpResponse<?> handle(HttpRequest request, AuthorizationException exception) {
    HttpRestExceptionBody exceptionBody = new HttpRestExceptionBody(
        CODE,
        MESSAGE
    );

    return HttpResponse.unauthorized().body(exceptionBody);

  }

}
