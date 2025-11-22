package org.hdschools.timebank.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hdschools.timebank.config.AuthenticationInterceptor;
import org.hdschools.timebank.model.ApiResponse;
import org.hdschools.timebank.model.Event;
import org.hdschools.timebank.model.StaValidatePointsRequest;
import org.hdschools.timebank.model.StaValidatePointsResponse;
import org.hdschools.timebank.model.StuDetails;
import org.hdschools.timebank.repository.EventRepository;
import org.hdschools.timebank.repository.StuDetailsRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles staff validation of student point update requests.
 * Creates "accepted" or "rejected" type entries in the event table.
 * Updates student details on accepted requests.
 */
@RestController
@RequestMapping("/sta")
@RequiredArgsConstructor
public class StaValidatePointsController {

    private final EventRepository eventRepository;
    private final StuDetailsRepository stuDetailsRepository;

    /**
     * Validates (accepts or rejects) a pending student point update request.
     * Creates a new event entry with type "accepted" or "rejected" based on staff decision.
     * If accepted, updates the student's accumulated points and credits.
     *
     * @param request the validation request containing decision and adjustments
     * @param httpRequest the HTTP request containing authentication information
     * @return {@link ApiResponse} containing the created validation event ID on success
     */
    @PostMapping("/validatePointsRequest")
    public ApiResponse<StaValidatePointsResponse> validatePointsRequest(
            @RequestBody StaValidatePointsRequest request,
            HttpServletRequest httpRequest) {
        
        // Extract authenticated staff ID from request attributes
        String staffId = (String) httpRequest.getAttribute(AuthenticationInterceptor.USER_ID_ATTRIBUTE);
        
        if (staffId == null) {
            return ApiResponse.error("Staff not authenticated", null);
        }

        // Fetch the original pending request to get student ID
        Event originalRequest = eventRepository.findById(request.getRequestId())
                .orElse(null);
        
        if (originalRequest == null) {
            return ApiResponse.error("Request not found", null);
        }
        
        if (originalRequest.getInitStuId() == null) {
            return ApiResponse.error("Invalid request: no student ID found", null);
        }

        // Determine event type based on acceptance
        String eventType = request.isAccepted() ? "accepted" : "rejected";

        // Create the validation event entry
        Event validationEvent = Event.builder()
                .initStaId(staffId)
                .pointDiff(request.getPointDiff())
                .creditDiff(request.getCreditDiff())
                .type(eventType)
                .contentHtml(request.getContentHtml())
                .build();

        // Save to database
        Event savedEvent = eventRepository.save(validationEvent);

        // Update student details if request is accepted
        if (request.isAccepted()) {
            String studentUserId = originalRequest.getInitStuId();
            StuDetails details = stuDetailsRepository.findByUserId(studentUserId)
                    .orElse(StuDetails.builder()
                            .userId(studentUserId)
                            .accumulatedPoints(0)
                            .accumulatedCredits(0)
                            .requestsMade(0)
                            .requestsApproved(0)
                            .totalPointAdditions(0)
                            .build());
            
            details.setAccumulatedPoints(details.getAccumulatedPoints() + request.getPointDiff());
            details.setAccumulatedCredits(details.getAccumulatedCredits() + request.getCreditDiff());
            details.setRequestsApproved(details.getRequestsApproved() + 1);
            
            // Add to totalPointAdditions only if pointDiff is positive
            if (request.getPointDiff() > 0) {
                details.setTotalPointAdditions(details.getTotalPointAdditions() + request.getPointDiff());
            }
            
            stuDetailsRepository.save(details);
        }

        // Return success response with event ID
        return ApiResponse.success(
                "Point update request " + eventType + " successfully",
                StaValidatePointsResponse.builder()
                        .eventId(savedEvent.getId())
                        .build()
        );
    }
}
