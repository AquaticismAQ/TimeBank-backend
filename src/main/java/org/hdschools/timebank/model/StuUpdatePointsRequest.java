package org.hdschools.timebank.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Request payload for student-initiated point update requests.
 * Contains the desired point change amount and WYSIWYG content.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StuUpdatePointsRequest extends AuthenticatedRequest {
    
    /**
     * The desired point change amount (can be positive or negative).
     */
    private int pointChange;
    
    /**
     * HTML content from the WYSIWYG editor describing the request.
     */
    private String contentHtml;
}
