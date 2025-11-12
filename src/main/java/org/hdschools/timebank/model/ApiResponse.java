package org.hdschools.timebank.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Generic wrapper for REST API responses.
 *
 * @param <T> type of the response payload
 */
@Data
@NoArgsConstructor
@Slf4j
public class ApiResponse<T> {

    private String status;
    private String message;
    private T data;

    /**
     * Creates an {@link ApiResponse} instance with the supplied values.
     *
     * @param status  response status, typically {@code success} or {@code error}
     * @param message human-readable description of the result
     * @param data    payload data for the caller
     */
    public ApiResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        log.info("ApiResponse created: status={}, message={}", status, message);
    }

    /**
     * Creates a successful response that wraps the provided payload.
     *
     * @param message confirmation message returned to the caller
     * @param data    payload data for the caller
     * @return successful {@link ApiResponse}
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("success", message, data);
    }

    /**
     * Creates an error response that wraps the provided payload.
     *
     * @param message confirmation message returned to the caller
     * @param data    payload data for the caller
     * @return successful {@link ApiResponse}
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>("error", message, data);
    }

}
