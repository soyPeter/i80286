package es.myinvestor.common.infrastructure.web.res;

import java.time.Instant;
import java.util.List;

/**
 * Generic API response wrapper.
 * Provides a consistent structure for all API responses.
 *
 * @param <T> the type of data in the response
 */
public record ApiResponse<T>(
    boolean success,
    T data,
    List<String> errors,
    Metadata metadata
) {
    /**
     * Creates a successful response with data.
     *
     * @param data the response data
     * @param <T>  the type of data
     * @return a new ApiResponse
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, new Metadata(Instant.now(), null, null, null, null));
    }

    /**
     * Creates a successful response with data and metadata.
     *
     * @param data     the response data
     * @param metadata the response metadata
     * @param <T>      the type of data
     * @return a new ApiResponse
     */
    public static <T> ApiResponse<T> success(T data, Metadata metadata) {
        return new ApiResponse<>(true, data, null, metadata);
    }

    /**
     * Creates an error response.
     *
     * @param errors the error messages
     * @param <T>    the type of data
     * @return a new ApiResponse
     */
    public static <T> ApiResponse<T> error(List<String> errors) {
        return new ApiResponse<>(false, null, errors, new Metadata(Instant.now(), null, null, null, null));
    }

    /**
     * Creates an error response with a single error message.
     *
     * @param error the error message
     * @param <T>   the type of data
     * @return a new ApiResponse
     */
    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse<>(false, null, List.of(error), new Metadata(Instant.now(), null, null, null, null));
    }

    /**
     * Metadata for API responses.
     */
    public record Metadata(
        Instant timestamp,
        Integer page,
        Integer size,
        Long totalElements,
        Integer totalPages
    ) {
        /**
         * Creates a new Metadata instance with the current timestamp.
         */
        public Metadata {
            if (timestamp == null) {
                timestamp = Instant.now();
            }
        }

        /**
         * Builder for Metadata.
         */
        public static class Builder {
            private Instant timestamp = Instant.now();
            private Integer page;
            private Integer size;
            private Long totalElements;
            private Integer totalPages;

            public Builder timestamp(Instant timestamp) {
                this.timestamp = timestamp;
                return this;
            }

            public Builder page(Integer page) {
                this.page = page;
                return this;
            }

            public Builder size(Integer size) {
                this.size = size;
                return this;
            }

            public Builder totalElements(Long totalElements) {
                this.totalElements = totalElements;
                return this;
            }

            public Builder totalPages(Integer totalPages) {
                this.totalPages = totalPages;
                return this;
            }

            public Metadata build() {
                return new Metadata(timestamp, page, size, totalElements, totalPages);
            }
        }

        /**
         * Creates a new Builder instance.
         *
         * @return a new Builder
         */
        public static Builder builder() {
            return new Builder();
        }
    }
}
