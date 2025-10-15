package io.bitnomio.shared.infra.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Record representing the standard response format for all API responses.
 *
 * @param <T> The type of data contained in the response
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record StandardResponse<T>(
    String code,
    String message,
    T data,
    Metadata metadata
) {
  private static final String SUCCESS_CODE = "SUCCESS";

  /**
   * Creates a success response with data and metadata.
   *
   * @param data     The response data
   * @param metadata The response metadata
   * @param <T>      The type of data
   * @return A new StandardResponse instance for a successful operation
   */
  public static <T> StandardResponse<T> success(T data, Metadata metadata) {
    return new StandardResponse<>(SUCCESS_CODE, null, data, metadata);
  }

  /**
   * Creates a success response with data and a message.
   *
   * @param data     The response data
   * @param message  A message describing the successful operation
   * @param metadata The response metadata
   * @param <T>      The type of data
   * @return A new StandardResponse instance for a successful operation with a message
   */
  public static <T> StandardResponse<T> success(T data, String message, Metadata metadata) {
    return new StandardResponse<>(SUCCESS_CODE, message, data, metadata);
  }

  /**
   * Creates an error response with an error code, message, and error details.
   *
   * @param code     The error code
   * @param message  A message describing the error
   * @param error    The error details (typically a ProblemDetail)
   * @param metadata The response metadata
   * @param <T>      The type of error details
   * @return A new StandardResponse instance for an error
   */
  public static <T> StandardResponse<T> error(String code, String message, T error, Metadata metadata) {
    return new StandardResponse<>(code, message, error, metadata);
  }

  /**
   * Creates an error response with a ProblemDetail.
   * The error code is derived from the ProblemDetail's title.
   *
   * @param problem  The ProblemDetail containing error information
   * @param metadata The response metadata
   * @return A new StandardResponse instance for an error with ProblemDetail
   */
  public static StandardResponse<ProblemDetail> error(ProblemDetail problem, Metadata metadata) {
    String errorCode = problem.title().toUpperCase().replace(' ', '_');
    return new StandardResponse<>(errorCode, problem.title(), problem, metadata);
  }


  public static class Builder<T> {
    private String code;
    private String message;
    private T data;
    private Metadata metadata;

    public Builder<T> code(String code) {
      this.code = code;
      return this;
    }

    public Builder<T> message(String message) {
      this.message = message;
      return this;
    }

    public Builder<T> data(T data) {
      this.data = data;
      return this;
    }

    public Builder<T> metadata(Metadata metadata) {
      this.metadata = metadata;
      return this;
    }

    public StandardResponse<T> build() {
      return new StandardResponse<>(code, message, data, metadata);
    }
  }

  public static <T> Builder<T> builder() {
    return new Builder<>();
  }


}
