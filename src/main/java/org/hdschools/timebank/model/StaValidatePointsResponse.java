package org.hdschools.timebank.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload for successful staff validation of point update requests.
 * Contains the ID of the validation event.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaValidatePointsResponse {
    
    /**
     * The ID of the created validation event entry.
     */
    private Long eventId;
}
