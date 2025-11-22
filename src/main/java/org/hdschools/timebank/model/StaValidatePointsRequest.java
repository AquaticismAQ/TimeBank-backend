package org.hdschools.timebank.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Request payload for staff validation of student point update requests.
 * Contains the request ID to validate, point/credit adjustments, and accept/reject indicator.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StaValidatePointsRequest extends AuthenticatedRequest {
    
    /**
     * The ID of the pending event/request to validate.
     */
    private Long requestId;
    
    /**
     * The staff-approved point differential.
     */
    private int pointDiff;
    
    /**
     * The staff-approved credit differential.
     */
    private int creditDiff;
    
    /**
     * Whether the request is accepted (true) or rejected (false).
     */
    private boolean accepted;
    
    /**
     * HTML content from the WYSIWYG editor with staff comments/notes.
     */
    private String contentHtml;
}
