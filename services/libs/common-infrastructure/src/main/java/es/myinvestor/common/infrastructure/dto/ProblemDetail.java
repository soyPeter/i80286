package es.myinvestor.common.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Record representing a Problem Detail according to RFC 7807.
 * Used for standardized error responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemDetail(
    String type,
    String title,
    Integer status,
    String detail,
    String instance,
    Map<String, Object> properties
) {
  /**
   * Creates a ProblemDetail instance with all fields.
   *
   * @param type       A URI reference that identifies the problem type
   * @param title      A short, human-readable summary of the problem type
   * @param status     The HTTP status code
   * @param detail     A human-readable explanation specific to this occurrence of the problem
   * @param instance   A URI reference that identifies the specific occurrence of the problem
   * @param properties Additional properties relevant to the error
   * @return A new ProblemDetail instance
   */
  public static ProblemDetail of(String type, String title, Integer status, String detail,
                                 String instance, Map<String, Object> properties) {
    return new ProblemDetail(type, title, status, detail, instance, properties);
  }

  /**
   * Creates a ProblemDetail instance without instance URI.
   *
   * @param type       A URI reference that identifies the problem type
   * @param title      A short, human-readable summary of the problem type
   * @param status     The HTTP status code
   * @param detail     A human-readable explanation specific to this occurrence of the problem
   * @param properties Additional properties relevant to the error
   * @return A new ProblemDetail instance without instance URI
   */
  public static ProblemDetail of(String type, String title, Integer status, String detail,
                                 Map<String, Object> properties) {
    return new ProblemDetail(type, title, status, detail, null, properties);
  }

  /**
   * Creates a ProblemDetail instance with minimal information.
   *
   * @param type   A URI reference that identifies the problem type
   * @param title  A short, human-readable summary of the problem type
   * @param status The HTTP status code
   * @param detail A human-readable explanation specific to this occurrence of the problem
   * @return A new ProblemDetail instance with minimal information
   */
  public static ProblemDetail of(String type, String title, Integer status, String detail) {
    return new ProblemDetail(type, title, status, detail, null, new HashMap<>());
  }

  /**
   * Creates a builder for constructing a ProblemDetail with additional properties.
   *
   * @param type   A URI reference that identifies the problem type
   * @param title  A short, human-readable summary of the problem type
   * @param status The HTTP status code
   * @return A new ProblemDetailBuilder
   */
  public static ProblemDetailBuilder builder(String type, String title, Integer status) {
    return new ProblemDetailBuilder(type, title, status);
  }

  /**
   * Builder class for constructing ProblemDetail instances with additional properties.
   */
  public static class ProblemDetailBuilder {
    private final String type;
    private final String title;
    private final Integer status;
    private String detail;
    private String instance;
    private final Map<String, Object> properties = new HashMap<>();

    private ProblemDetailBuilder(String type, String title, Integer status) {
      this.type = type;
      this.title = title;
      this.status = status;
    }

    public ProblemDetailBuilder detail(String detail) {
      this.detail = detail;
      return this;
    }

    public ProblemDetailBuilder instance(String instance) {
      this.instance = instance;
      return this;
    }

    public ProblemDetailBuilder property(String key, Object value) {
      this.properties.put(key, value);
      return this;
    }

    public ProblemDetail build() {
      return new ProblemDetail(type, title, status, detail, instance, properties);
    }
  }
}
