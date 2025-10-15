package io.bitnomio.shared.infra.data.dto;

/**
 * Enum representing the type of client making the request to the API.
 * This classification is used to adapt responses, apply specific rate limiting,
 * and optimize content delivery based on the client type. The type is primarily
 * determined by analyzing the User-Agent header of incoming requests.
 */

public enum ClientType {
  /**
   * Web browser client (Chrome, Firefox, Safari, Edge, etc.).
   * Typically expects HTML/JS responses and full web interface.
   */
  WEB,

  /**
   * Mobile application client (Android, iOS, etc.).
   * Optimized for mobile devices and touch interfaces.
   */
  MOBILE,

  /**
   * Command-line or terminal-based client (curl, wget, etc.).
   * Usually expects plain text or structured data responses.
   */
  TERMINAL,

  /**
   * API development and testing tools (Postman, RESTlet, etc.).
   * Expects raw API responses without UI optimization.
   */
  REST_CLIENT,

  /**
   * Unknown client type.
   * Used when the client type cannot be determined from the User-Agent.
   */
  UNKNOWN;

  /**
   * Determines the client type by analyzing the User-Agent header string.
   * The method performs case-insensitive pattern matching against known
   * User-Agent signatures to categorize the client into one of the defined types.
   * The detection follows this priority order:
   * 1. Mobile devices (Android, iOS, etc.)
   * 2. Web browsers (Chrome, Firefox, etc.)
   * 3. Terminal clients (curl, wget, etc.)
   * 4. REST clients (Postman, RESTlet, etc.)
   * 5. Unknown (when no patterns match)
   *
   * @param userAgent the User-Agent header value from the HTTP request
   * @return the determined ClientType, or UNKNOWN if the type cannot be determined
   */
  public static ClientType fromUserAgent(String userAgent) {
    if (userAgent == null || userAgent.isEmpty()) {
      return UNKNOWN;
    }

    String lowerCaseUserAgent = userAgent.toLowerCase();

    // Check for mobile devices
    if (lowerCaseUserAgent.contains("android") ||
        lowerCaseUserAgent.contains("iphone") ||
        lowerCaseUserAgent.contains("ipad") ||
        lowerCaseUserAgent.contains("ipod") ||
        lowerCaseUserAgent.contains("mobile") ||
        lowerCaseUserAgent.contains("blackberry") ||
        lowerCaseUserAgent.contains("windows phone") ||
        lowerCaseUserAgent.contains("opera mini") ||
        lowerCaseUserAgent.contains("opera mobi") ||
        lowerCaseUserAgent.contains("webos")) {
      return MOBILE;
    }

    // Check for common web browsers
    if (lowerCaseUserAgent.contains("mozilla") ||
        lowerCaseUserAgent.contains("chrome") ||
        lowerCaseUserAgent.contains("safari") ||
        lowerCaseUserAgent.contains("edge") ||
        lowerCaseUserAgent.contains("firefox") ||
        lowerCaseUserAgent.contains("opera")) {
      return WEB;
    }

    if (lowerCaseUserAgent.contains("curl") ||
        lowerCaseUserAgent.contains("wget") ||
        lowerCaseUserAgent.contains("httpie") ||
        lowerCaseUserAgent.contains("iterm")) {

      return TERMINAL;

    }

    if (lowerCaseUserAgent.contains("postman") ||
        lowerCaseUserAgent.contains("restlet") ||
        lowerCaseUserAgent.contains("restclient")) {
      return REST_CLIENT;
    }

    // Default to UNKNOWN for unrecognized user agents
    return UNKNOWN;
  }
}
