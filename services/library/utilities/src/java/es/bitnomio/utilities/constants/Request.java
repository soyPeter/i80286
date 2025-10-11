package es.bitnomio.utilities.constants;

import io.micronaut.core.type.Argument;

// This interface defines constants for HTTP requests
public interface Request {

  // This interface defines constants for the body of the request
  interface Body {

    // The argument type for a request body as a String
    Argument<String> BODY_AS_STRING = Argument.of(String.class);
  }

  // This interface defines constants for the headers of the request
  interface Header {

    // Constant for the "Authentication" header field
    String AUTHENTICATION = "Authentication";
    // Constant for the "app-version" header field
    String APP_VERSION = "app-version";
    // Constant for the "dashboard-version" header field
    String DASHBOARD_VERSION = "dashboard-version";
    // Constant for the "device-udid" header field
    String DEVICE_UDID = "device-udid";
    // Constant for the "req-id" header field
    String REQUEST_ID = "req-id";
    // Constant for the "req-user" header field
    String REQUEST_USER = "req-user";
    // Constant for the "time-zone" header field
    String TIME_ZONE = "time-zone";
    // Constant for the "user-locale" header field
    String USER_LOCALE = "user-locale";
  }
}
