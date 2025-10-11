package es.bitnomio.utilities.security;

import es.bitnomio.utilities.constants.AppConfig;
import es.bitnomio.utilities.exceptions.dtos.HttpRestExceptionBody;
import es.bitnomio.utilities.persistence.redis.CacheRedisUtils;
import es.bitnomio.utilities.utils.JWTUtils;
import es.bitnomio.utilities.utils.MDCUtils;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Order;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.FilterChain;
import io.micronaut.http.filter.HttpFilter;
import jakarta.inject.Inject;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static es.bitnomio.utilities.utils.JWTUtils.BEARER_MARKER;

/**
 * TokenBannedServerRequestFilter checks if the user has any banned tokens before proceeding with the HTTP request.
 * This filter is applied to all incoming HTTP requests and uses cache data to verify token validity.
 * If a banned token is found, an UnauthorizedException is thrown.
 * The filter will only be applied when CacheRedisUtils bean is present in the context.
 */
@Filter("/**")
@Requires(property = "micronaut.redis.uri")
@Requires(beans = CacheRedisUtils.class)
@Order(1)
public class TokenBannedServerRequestFilter implements HttpFilter {

  @Inject
  CacheRedisUtils cacheRedisUtils;

  private static final Logger logger = LoggerFactory.getLogger(TokenBannedServerRequestFilter.class);

  /**
   * Filters the HTTP request to check for banned tokens.
   *
   * @param request the HTTP request
   * @param chain   the filter chain
   * @return a Publisher for the HTTP response
   */
  @Override
  public Publisher<? extends HttpResponse<?>> doFilter(HttpRequest<?> request, FilterChain chain) {
    // Step 1: Create a Mono Publisher that defers execution
    return Mono.defer(() -> {
      // Step 2: Retrieve the username from the MDC context
      String username = MDCUtils.getUser();
      logger.info("Checking if user: {} has banned tokens", username);

      // Step 3: Retrieve banned token information from the cache
      Optional<Object> optionalData = cacheRedisUtils
          .getDataFromFolder(AppConfig.Cache.Folders.TOKENS_BANNED, username);

      // Step 4: Map the Optional data to a String, defaulting to an empty string if not present
      String data = optionalData.map(Object::toString).orElse("");

      // Step 5: If banned token data is found in the cache
      if (!data.isEmpty()) {
        logger.warn("User: {} has banned tokens.", username);

        // Step 6: Retrieve and clean the bearer token from the request header
        String bearerToken = JWTUtils.cleanBearerMarkerFromToken(request.getHeaders().get("Authorization"));

        // Step 7: If bearer token is not empty and contains banned token data
        if (!bearerToken.isEmpty() && bearerToken.contains(data)) {
          logger.warn("User: {} has provided a banned token.", username);

          // Step 8: Return an unauthorized response with a custom body
          return Mono.just(HttpResponse.status(HttpStatus.UNAUTHORIZED)
              .body(new HttpRestExceptionBody("INVALID_TOKEN", "Token banned for user.")));
        }
      }

      // Step 9: Proceed with the HTTP request if no banned tokens are found
      return Mono.from(chain.proceed(request));
    });
  }
}
