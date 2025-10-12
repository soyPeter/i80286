package es.myinvestor.common.infrastructure.dto;

import io.hypersistence.tsid.TSID;

import java.util.Map;

/**
 * Value object representing the context of a request.
 * Contains information about the client, request details, and additional metadata.
 */
public record RequestContext(
    /**
     * Unique identifier for the request.
     */
    String requestId,

    /**
     * Type of client making the request.
     */
    ClientType clientType,

    /**
     * Original path of the request.
     */
    String path,

    /**
     * HTTP method of the request.
     */
    String method,

    /**
     * Headers from the original request.
     */
    Map<String, String> headers,

    /**
     * Query parameters from the original request.
     */
    Map<String, String> queryParams,

    /**
     * Additional metadata for the request.
     */
    Map<String, Object> metadata
) {
  /**
   * Creates a new RequestContext with a generated request ID.
   *
   * @param clientType  the type of client making the request
   * @param path        the original path of the request
   * @param method      the HTTP method of the request
   * @param headers     the headers from the original request
   * @param queryParams the query parameters from the original request
   * @param metadata    additional metadata for the request
   * @return a new RequestContext
   */
  public static RequestContext create(
      ClientType clientType,
      String path,
      String method,
      Map<String, String> headers,
      Map<String, String> queryParams,
      Map<String, Object> metadata) {
    return new RequestContext(
        TSID.Factory.getTsid().toString(),
        clientType,
        path,
        method,
        headers,
        queryParams,
        metadata
    );
  }
}
