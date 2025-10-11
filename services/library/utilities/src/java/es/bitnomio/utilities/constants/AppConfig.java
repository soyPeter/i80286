package es.bitnomio.utilities.constants;

/**
 * The {@code Bitnomio} interface contains nested interfaces with constant definitions used in the Bitnomio application.
 * <p>
 * The constants defined here are primarily used for identifying marker strings, defining metadata keys, and specifying
 * endpoint paths.
 * </p>
 */
public interface AppConfig {

  /**
   * The {@code Markers} interface contains string constants
   * used as markers or delimiters in documents and data processing.
   */
  interface Markers {

    /** Marker for Bitnomio system identifier */
    String BITNOMIO = "BITNOMI0";

    /** Marker used to split string segments */
    String SPLIT = "_#_";

    /**
     * Marker constants used for common delimiters
     * in string processing.
     */
    String COMMA = ",";
    String COLON = ":";
    String SEMICOLON = ";";
    String PERIOD = ".";
    String PIPE = "|";
    String PARENTHESIS_CLOSE = ")";
    String PARENTHESIS_OPEN = "(";
  }

  /**
   * The {@code MDC} interface contains constants representing
   * keys for mapped diagnostic contexts used in logging.
   */
  interface MDC {

    String SERVICE_NAME = "serviceName";
    String REQUEST_ID = "requestId";
    String REQUEST_USER = "requestUser";
    String DEFAULT_USER = "bitnomio-service";
  }

  interface Cache {
    interface Folders {
        String USERS_BLOCKED = "users:blocked";
        String TOKENS_BANNED = "tokens:banned";
    }
  }

  /**
   * The {@code Endpoints} interface contains string constants
   * representing API endpoint paths.
   */
  interface Endpoints {

    String API_MARKER = "/api";
    String LOGIN_MARKER = "/auth/login";
  }
}
