package io.bitnomio.shared.infra.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Record representing pagination information.
 * Used in StandardResponse metadata to provide pagination details.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PageInfo(
    Integer page,
    Integer size,
    Long totalElements,
    Integer totalPages,
    Boolean hasNext,
    Boolean hasPrevious
) {
  /**
   * Creates a PageInfo instance with all fields.
   *
   * @param page          Current page number (0-based)
   * @param size          Page size
   * @param totalElements Total number of elements
   * @param totalPages    Total number of pages
   * @param hasNext       Whether there is a next page
   * @param hasPrevious   Whether there is a previous page
   * @return A new PageInfo instance
   */
  public static PageInfo of(Integer page, Integer size, Long totalElements, Integer totalPages,
                            Boolean hasNext, Boolean hasPrevious) {
    return new PageInfo(page, size, totalElements, totalPages, hasNext, hasPrevious);
  }

  /**
   * Creates a PageInfo instance with calculated hasNext and hasPrevious values.
   *
   * @param page          Current page number (0-based)
   * @param size          Page size
   * @param totalElements Total number of elements
   * @return A new PageInfo instance with calculated fields
   */
  public static PageInfo of(Integer page, Integer size, Long totalElements) {
    if (page == null || size == null || totalElements == null) {
      return null;
    }

    int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
    boolean hasNext = page < totalPages - 1;
    boolean hasPrevious = page > 0;

    return new PageInfo(page, size, totalElements, totalPages, hasNext, hasPrevious);
  }

  /**
   * Creates a PageInfo instance from common pagination headers.
   *
   * @param page          Current page number (0-based)
   * @param size          Page size
   * @param totalElements Total number of elements
   * @return A new PageInfo instance
   */
  public static PageInfo fromHeaders(Integer page, Integer size, Long totalElements) {
    return of(page, size, totalElements);
  }
}