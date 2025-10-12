package es.myinvestor.common.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * Record representing metadata for standard responses in the API.
 * This class encapsulates common metadata information used across API responses including:
 * - Request ID: Unique identifier for tracking requests
 * - Pagination: Information about page size, current page, and total elements
 * - Timestamp: Unix timestamp of when the response was generated
 * - Version: API version identifier
 * - Others: Additional key-value pairs for extensibility
 * <p>
 * The class provides several factory methods for convenient instantiation with default values.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Metadata(
    String requestId,
    PageInfo pagination,
    Long timestamp,
    String version,
    Map<String, String> info
) {

  public static Builder from() {
    return new Builder();
  }

  public static class Builder {
    private String requestId;
    private PageInfo pagination;
    private Long timestamp = System.currentTimeMillis();
    private String version = DEFAULT_VERSION;
    private Map<String, String> info;

    public Builder requestId(String requestId) {
      this.requestId = requestId;
      return this;
    }

    public Builder pagination(PageInfo pagination) {
      this.pagination = pagination;
      return this;
    }

    public Builder timestamp(Long timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder version(String version) {
      this.version = version;
      return this;
    }

    public Builder others(Map<String, String> others) {
      this.info = others;
      return this;
    }

    public Metadata build() {
      return new Metadata(requestId, pagination, timestamp, version, info);
    }
  }

  private static final String DEFAULT_VERSION = "1.0";

  /**
   * Creates a Metadata instance with all fields.
   *
   * @param requestId  Unique identifier for the request
   * @param pagination Pagination information
   * @param timestamp  Timestamp of the response
   * @param version    API version
   * @param others     Additional metadata key-value pairs
   * @return A new Metadata instance
   */
  public static Metadata of(String requestId, PageInfo pagination, Long timestamp, String version, Map<String, String> others) {
    return from()
        .requestId(requestId)
        .pagination(pagination)
        .timestamp(timestamp)
        .version(version)
        .others(others)
        .build();
  }

  /**
   * Creates a Metadata instance with default version.
   *
   * @param requestId  Unique identifier for the request
   * @param pagination Pagination information
   * @param timestamp  Timestamp of the response
   * @param others     Additional metadata key-value pairs
   * @return A new Metadata instance with default version
   */
  public static Metadata of(String requestId, PageInfo pagination, Long timestamp, Map<String, String> others) {
    return from()
        .requestId(requestId)
        .pagination(pagination)
        .timestamp(timestamp)
        .others(others)
        .build();
  }

  /**
   * Creates a Metadata instance with current timestamp and default version.
   *
   * @param requestId  Unique identifier for the request
   * @param pagination Pagination information
   * @param others     Additional metadata key-value pairs
   * @return A new Metadata instance with current timestamp and default version
   */
  public static Metadata of(String requestId, PageInfo pagination, Map<String, String> others) {
    return from()
        .requestId(requestId)
        .pagination(pagination)
        .others(others)
        .build();
  }

  /**
   * Creates a Metadata instance with current timestamp and default version.
   *
   * @param requestId  Unique identifier for the request
   * @param pagination Pagination information
   * @return A new Metadata instance with current timestamp and default version
   */
  public static Metadata of(String requestId, PageInfo pagination) {
    return from()
        .requestId(requestId)
        .pagination(pagination)
        .build();
  }

  /**
   * Creates a Metadata instance without pagination information.
   *
   * @param requestId Unique identifier for the request
   * @return A new Metadata instance without pagination
   */
  public static Metadata of(String requestId) {
    return from()
        .requestId(requestId)
        .build();
  }


}
