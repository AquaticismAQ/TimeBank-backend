package org.hdschools.timebank.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hdschools.timebank.config.AuthenticationInterceptor;
import org.hdschools.timebank.model.ApiResponse;
import org.hdschools.timebank.model.Event;
import org.hdschools.timebank.model.StuUpdatePointsRequest;
import org.hdschools.timebank.model.StuUpdatePointsResponse;
import org.hdschools.timebank.repository.EventRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles student-initiated point update requests.
 * Creates "pending" type entries in the event table.
 */
@RestController
@RequestMapping("/stu")
@RequiredArgsConstructor
public class StuUpdatePointsController {

    private final EventRepository eventRepository;

    /**
     * Creates a point update request initiated by a student.
     * The request is stored as a "pending" type event in the database.
     *
     * @param request the point update request containing point change and content
     * @param httpRequest the HTTP request containing authentication information
     * @return {@link ApiResponse} containing the created event ID on success
     */
    @PostMapping("/updatePointsRequest")
    public ApiResponse<StuUpdatePointsResponse> createUpdatePointsRequest(
            @RequestBody StuUpdatePointsRequest request,
            HttpServletRequest httpRequest) {
        
        // Extract authenticated user ID from request attributes
        String userId = (String) httpRequest.getAttribute(AuthenticationInterceptor.USER_ID_ATTRIBUTE);
        
        if (userId == null) {
            return ApiResponse.error("User not authenticated", null);
        }

        // Create the event entry
        Event event = Event.builder()
                .initStuId(userId)
                .pointDiff(request.getPointChange())
                .creditDiff(0)
                .type("pending")
                .contentHtml(request.getContentHtml())
                .build();

        // Save to database
        Event savedEvent = eventRepository.save(event);

        // Return success response with event ID
        return ApiResponse.success(
                "Point update request created successfully",
                StuUpdatePointsResponse.builder()
                        .eventId(savedEvent.getId())
                        .build()
        );
    }
}
