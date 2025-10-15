package io.bitnomio.shared.infra.utils;

import org.springframework.web.server.ServerWebExchange;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Utility class for extracting service names from various sources.
 * Provides optimized methods with caching for better performance.
 */
public final class ServiceNameExtractor {

  private static final String DEFAULT_UNKNOWN_SERVICE = "unknown";
  private static final String DEFAULT_SERVICE_SUFFIX = "-service";

  // Compiled regex pattern for better performance
  private static final Pattern API_PATH_PATTERN = Pattern.compile("^/api/([^/]+).*$");

  // Cache for path to service name mapping
  private static final Map<String, String> PATH_SERVICE_CACHE = new ConcurrentHashMap<>();

  // Cache size limit to prevent memory issues
  private static final int MAX_CACHE_SIZE = 1000;

  // Private constructor to prevent instantiation
  private ServiceNameExtractor() {
    throw new AssertionError("Utility class should not be instantiated");
  }

  /**
   * Extracts service name from a path.
   * For paths like /api/accounts/..., returns "accounts-service"
   *
   * @param path The request path
   * @return The extracted service name or "unknown" if not found
   */
  public static String fromPath(String path) {
    return fromPath(path, DEFAULT_UNKNOWN_SERVICE);
  }

  /**
   * Extracts service name from a path with custom default value.
   *
   * @param path         The request path
   * @param defaultValue The default value to return if service name cannot be extracted
   * @return The extracted service name or the provided default value if not found
   */
  public static String fromPath(String path, String defaultValue) {
    if (path == null) {
      return defaultValue;
    }

    // Check cache first
    String cachedResult = PATH_SERVICE_CACHE.get(path);
    if (cachedResult != null) {
      return cachedResult;
    }

    // Extract service name
    String result;
    if (path.startsWith("/api/")) {
      String[] parts = path.split("/");
      if (parts.length >= 3) {
        result = parts[2] + DEFAULT_SERVICE_SUFFIX;
        cacheResult(path, result);
        return result;
      }
    }

    return defaultValue;
  }

  /**
   * Extracts service name from a ServerWebExchange.
   * For WebFlux applications, extracts from path since we don't have gateway routes.
   *
   * @param exchange The server web exchange
   * @return The extracted service name or "unknown" if not found
   */
  public static String fromExchange(ServerWebExchange exchange) {
    return fromExchange(exchange, DEFAULT_UNKNOWN_SERVICE);
  }

  /**
   * Extracts service name from a ServerWebExchange with custom default value.
   *
   * @param exchange     The server web exchange
   * @param defaultValue The default value to return if service name cannot be extracted
   * @return The extracted service name or the provided default value if not found
   */
  public static String fromExchange(ServerWebExchange exchange, String defaultValue) {
    if (exchange == null) {
      return defaultValue;
    }

    // For WebFlux without Gateway, extract from path
    String path = exchange.getRequest().getPath().value();
    return fromPath(path, defaultValue);
  }

  /**
   * Caches a path to service name mapping.
   * Implements a simple eviction strategy if cache gets too large.
   *
   * @param path        The path
   * @param serviceName The service name
   */
  private static void cacheResult(String path, String serviceName) {
    // Simple cache eviction strategy
    if (PATH_SERVICE_CACHE.size() >= MAX_CACHE_SIZE) {
      // Clear a portion of the cache when it gets too large
      PATH_SERVICE_CACHE.clear();
    }

    PATH_SERVICE_CACHE.put(path, serviceName);
  }

  /**
   * Clears the internal cache.
   * Useful for testing or when configuration changes.
   */
  public static void clearCache() {
    PATH_SERVICE_CACHE.clear();
  }
}