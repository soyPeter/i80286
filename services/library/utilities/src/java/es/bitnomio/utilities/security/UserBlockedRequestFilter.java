package es.bitnomio.utilities.security;

import es.bitnomio.utilities.constants.AppConfig;
import es.bitnomio.utilities.exceptions.dtos.HttpRestExceptionBody;
import es.bitnomio.utilities.persistence.redis.CacheRedisUtils;
import es.bitnomio.utilities.utils.JsonUtils;
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

import java.time.LocalDateTime;
import java.util.Map;

/**
 * This filter intercepts all HTTP requests to check if the user is banned or blocked.
 */
@Filter("/**")
@Requires(property = "micronaut.redis.uri")
@Requires(beans = CacheRedisUtils.class)
@Order(1)
public class UserBlockedRequestFilter implements HttpFilter {

  private static final Logger logger = LoggerFactory.getLogger(UserBlockedRequestFilter.class);

  private final CacheRedisUtils cacheRedisUtils;

  /**
   * Constructs a new UserBlockedRequestFilter with the given CacheRedisUtils.
   *
   * @param cacheRedisUtils the CacheRedisUtils to fetch blocked user data
   */
  @Inject
  public UserBlockedRequestFilter(CacheRedisUtils cacheRedisUtils) {
    this.cacheRedisUtils = cacheRedisUtils;
  }

  /**
   * This method checks if the user is banned or blocked. If the user is not blocked, the request proceeds; otherwise,
   * an UNAUTHORIZED response is returned.
   *
   * @param request the HTTP request to be filtered
   * @param chain   the filter chain
   * @return a Publisher with the HTTP response
   */
  @Override
  public Publisher<? extends HttpResponse<?>> doFilter(HttpRequest<?> request, FilterChain chain) {

    // Step 1: Log the beginning of user block check
    logger.info("Checking if user is banned or blocked");

    // Step 2: Define a default value for blockedUntil to handle null case
    LocalDateTime blockedUntil = LocalDateTime.now().minusSeconds(1);

    try {
      // Step 3: Retrieve the username from the MDC context
      String username = MDCUtils.getUser();
      logger.info("Checking if user: {} is banned or blocked", username);

      // Step 4: Retrieve user blocked data from the cache
      String data = (String) cacheRedisUtils
          .getDataFromFolder(AppConfig.Cache.Folders.USERS_BLOCKED, username)
          .orElse("");

      // Step 5: Parse the retrieved JSON data into a Map
      Map<String, Object> parsedData = JsonUtils.jsonToMap(data);

      // Step 6: Check if the parsed data contains an expiration date (exp_date)
      if (parsedData.containsKey("exp_date")) {
        // Step 7: Parse the expiration date string to LocalDateTime
        String expDateString = parsedData.get("exp_date").toString();
        blockedUntil = LocalDateTime.parse(expDateString);
      } else {
        logger.warn("exp_date not found in the data");
      }

    } catch (Exception e) {
      // Step 8: Log any errors encountered during data retrieval from the cache
      logger.error("Error retrieving user blocked data from cache: {}", e.getMessage(), e);
    }

    // Step 9: Compare the current time with blockedUntil
    if (blockedUntil.isAfter(LocalDateTime.now())) {
      // Step 10: Log and return an UNAUTHORIZED response if the user is still blocked
      logger.warn("User is banned or blocked until {}", blockedUntil);
      return Mono.just(HttpResponse.status(HttpStatus.UNAUTHORIZED).body(new HttpRestExceptionBody(
          "USER_BLOCKED", "User is banned or blocked until " + blockedUntil)));
    }

    // Step 11: Proceed with the request if the user is not blocked
    return chain.proceed(request);
  }
}
