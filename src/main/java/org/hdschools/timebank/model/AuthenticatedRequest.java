package org.hdschools.timebank.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Base class for API requests that require authentication.
 * Includes a session token field for authenticated endpoints.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuthenticatedRequest extends ApiRequest {
    private String token;
}
