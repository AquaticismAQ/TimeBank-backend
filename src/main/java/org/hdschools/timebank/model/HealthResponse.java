package org.hdschools.timebank.model;

public class HealthResponse extends ApiResponse<String> {
    public HealthResponse(String status, String message, String data) {
        super(status, message, data);
    }

    public static ApiResponse<String> healthy() {
        return HealthResponse.success("OK", "Service is healthy");
    }
}
