package org.hdschools.timebank.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hdschools.timebank.model.Event;
import org.hdschools.timebank.model.StuDetails;
import org.hdschools.timebank.repository.EventRepository;
import org.hdschools.timebank.repository.StuDetailsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for scheduled recalculation of student details.
 * Runs every Monday at 00:00 to ensure data accuracy.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceRecalculationService {

    private final EventRepository eventRepository;
    private final StuDetailsRepository stuDetailsRepository;

    private static final int INITIAL_POINTS = 0;
    private static final int INITIAL_CREDITS = 100;

    /**
     * Recalculates all student details based on event history.
     * Scheduled to run every Monday at 00:00 (midnight).
     * <p>
     * Points start at 0 and only change with "accepted" type events.
     * Credits start at 100 and change with both "accepted" and "rejected" type events.
     * Also calculates: requests made (pending+accepted+rejected), requests approved, and total point additions.
     */
    @Scheduled(cron = "0 0 0 * * MON")
    @Transactional
    public void recalculateAllBalances() {
        log.info("Starting scheduled details recalculation...");
        
        try {
            // Calculate points from "accepted" events only
            Map<String, Integer> pointsByStudent = calculatePointsByStudent();
            
            // Calculate credits from "accepted" and "rejected" events
            Map<String, Integer> creditsByStudent = calculateCreditsByStudent();
            
            // Calculate request statistics
            Map<String, Integer> requestsMadeByStudent = calculateRequestsMadeByStudent();
            Map<String, Integer> requestsApprovedByStudent = calculateRequestsApprovedByStudent();
            Map<String, Integer> pointAdditionsByStudent = calculatePointAdditionsByStudent();
            
            // Get all unique student user IDs
            Map<String, StuDetails> detailsToSave = new HashMap<>();
            
            // Collect all student user IDs from all maps
            for (String userId : requestsMadeByStudent.keySet()) {
                if (!detailsToSave.containsKey(userId)) {
                    detailsToSave.put(userId, createOrLoadDetails(userId));
                }
            }
            for (String userId : pointsByStudent.keySet()) {
                if (!detailsToSave.containsKey(userId)) {
                    detailsToSave.put(userId, createOrLoadDetails(userId));
                }
            }
            for (String userId : creditsByStudent.keySet()) {
                if (!detailsToSave.containsKey(userId)) {
                    detailsToSave.put(userId, createOrLoadDetails(userId));
                }
            }
            
            // Update all details
            for (StuDetails details : detailsToSave.values()) {
                String userId = details.getUserId();
                
                int points = pointsByStudent.getOrDefault(userId, 0);
                int credits = creditsByStudent.getOrDefault(userId, 0);
                int requestsMade = requestsMadeByStudent.getOrDefault(userId, 0);
                int requestsApproved = requestsApprovedByStudent.getOrDefault(userId, 0);
                int pointAdditions = pointAdditionsByStudent.getOrDefault(userId, 0);
                
                details.setAccumulatedPoints(INITIAL_POINTS + points);
                details.setAccumulatedCredits(INITIAL_CREDITS + credits);
                details.setRequestsMade(requestsMade);
                details.setRequestsApproved(requestsApproved);
                details.setTotalPointAdditions(pointAdditions);
            }
            
            // Save all details
            if (!detailsToSave.isEmpty()) {
                stuDetailsRepository.saveAll(detailsToSave.values());
                log.info("Details recalculation completed successfully. Updated {} student records.", 
                        detailsToSave.size());
            } else {
                log.info("No student details to recalculate.");
            }
            
        } catch (Exception e) {
            log.error("Error during balance recalculation", e);
        }
    }

    /**
     * Calculates total points per student from "accepted" events.
     *
     * @return map of student user ID to total points
     */
    private Map<String, Integer> calculatePointsByStudent() {
        List<Event> acceptedEvents = eventRepository.findAllAcceptedByStudents();
        Map<String, Integer> pointsMap = new HashMap<>();
        
        for (Event event : acceptedEvents) {
            String userId = event.getInitStuId();
            pointsMap.put(userId, pointsMap.getOrDefault(userId, 0) + event.getPointDiff());
        }
        
        return pointsMap;
    }

    /**
     * Calculates total credits per student from "accepted" and "rejected" events.
     *
     * @return map of student user ID to total credits
     */
    private Map<String, Integer> calculateCreditsByStudent() {
        List<Event> creditEvents = eventRepository.findAllAcceptedOrRejectedByStudents();
        Map<String, Integer> creditsMap = new HashMap<>();
        
        for (Event event : creditEvents) {
            String userId = event.getInitStuId();
            creditsMap.put(userId, creditsMap.getOrDefault(userId, 0) + event.getCreditDiff());
        }
        
        return creditsMap;
    }

    /**
     * Calculates total requests made per student (pending, accepted, and rejected).
     *
     * @return map of student user ID to total requests made
     */
    private Map<String, Integer> calculateRequestsMadeByStudent() {
        List<Event> allEvents = eventRepository.findAll();
        Map<String, Integer> requestsMap = new HashMap<>();
        
        for (Event event : allEvents) {
            if (event.getInitStuId() != null && 
                ("pending".equals(event.getType()) || "accepted".equals(event.getType()) || "rejected".equals(event.getType()))) {
                String userId = event.getInitStuId();
                requestsMap.put(userId, requestsMap.getOrDefault(userId, 0) + 1);
            }
        }
        
        return requestsMap;
    }

    /**
     * Calculates total approved requests per student.
     *
     * @return map of student user ID to total approved requests
     */
    private Map<String, Integer> calculateRequestsApprovedByStudent() {
        List<Event> acceptedEvents = eventRepository.findAllAcceptedByStudents();
        Map<String, Integer> approvedMap = new HashMap<>();
        
        for (Event event : acceptedEvents) {
            String userId = event.getInitStuId();
            approvedMap.put(userId, approvedMap.getOrDefault(userId, 0) + 1);
        }
        
        return approvedMap;
    }

    /**
     * Calculates total point additions per student (only positive point changes from accepted events).
     *
     * @return map of student user ID to total point additions
     */
    private Map<String, Integer> calculatePointAdditionsByStudent() {
        List<Event> acceptedEvents = eventRepository.findAllAcceptedByStudents();
        Map<String, Integer> additionsMap = new HashMap<>();
        
        for (Event event : acceptedEvents) {
            String userId = event.getInitStuId();
            if (event.getPointDiff() > 0) {
                additionsMap.put(userId, additionsMap.getOrDefault(userId, 0) + event.getPointDiff());
            }
        }
        
        return additionsMap;
    }

    /**
     * Creates a new StuDetails or loads existing one for the given user ID.
     *
     * @param userId the student user ID
     * @return StuDetails instance
     */
    private StuDetails createOrLoadDetails(String userId) {
        return stuDetailsRepository.findByUserId(userId)
                .orElse(StuDetails.builder()
                        .userId(userId)
                        .accumulatedPoints(0)
                        .accumulatedCredits(0)
                        .requestsMade(0)
                        .requestsApproved(0)
                        .totalPointAdditions(0)
                        .build());
    }
}
