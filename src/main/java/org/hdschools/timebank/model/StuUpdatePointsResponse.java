package org.hdschools.timebank.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload for successful student point update request creation.
 * Contains the ID of the created event.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StuUpdatePointsResponse {
    
    /**
     * The ID of the created event entry.
     */
    private Long eventId;
}
