package org.hdschools.timebank.model;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base class for all API request payloads.
 */
@Data
@NoArgsConstructor
public class ApiRequest {
    private Instant timestamp;
}
