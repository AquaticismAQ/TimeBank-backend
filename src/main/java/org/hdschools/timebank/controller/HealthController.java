package org.hdschools.timebank.controller;

import org.hdschools.timebank.model.ApiResponse;
import org.hdschools.timebank.model.HealthResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes health-check endpoints for clients to verify service availability.
 */
@RestController
public class HealthController {

    /**
     * Returns a simple success payload indicating the service is operational.
     *
     * @return {@link ApiResponse} containing health details
     */
    @PostMapping("/health")
    public ApiResponse<String> health() {
        return HealthResponse.success("Service is healthy", "OK");
    }

}
