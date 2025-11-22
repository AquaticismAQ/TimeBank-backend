package org.hdschools.timebank.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hdschools.timebank.config.AuthenticationInterceptor;
import org.hdschools.timebank.model.ApiResponse;
import org.hdschools.timebank.model.Event;
import org.hdschools.timebank.model.StaValidatePointsRequest;
import org.hdschools.timebank.model.StaValidatePointsResponse;
import org.hdschools.timebank.model.StuBalance;
import org.hdschools.timebank.repository.EventRepository;
import org.hdschools.timebank.repository.StuBalanceRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles staff validation of student point update requests.
 * Creates "accepted" or "rejected" type entries in the event table.
 * Updates student balance on accepted requests.
 */
@RestController
@RequestMapping("/sta")
@RequiredArgsConstructor
public class StaValidatePointsController {

    private final EventRepository eventRepository;
    private final StuBalanceRepository stuBalanceRepository;

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

        // Update student balance if request is accepted
        if (request.isAccepted()) {
            String studentUserId = originalRequest.getInitStuId();
            StuBalance balance = stuBalanceRepository.findByUserId(studentUserId)
                    .orElse(StuBalance.builder()
                            .userId(studentUserId)
                            .accumulatedPoints(0)
                            .accumulatedCredits(0)
                            .build());
            
            balance.setAccumulatedPoints(balance.getAccumulatedPoints() + request.getPointDiff());
            balance.setAccumulatedCredits(balance.getAccumulatedCredits() + request.getCreditDiff());
            stuBalanceRepository.save(balance);
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
