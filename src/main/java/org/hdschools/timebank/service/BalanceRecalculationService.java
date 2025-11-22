package org.hdschools.timebank.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hdschools.timebank.model.Event;
import org.hdschools.timebank.model.StuBalance;
import org.hdschools.timebank.repository.EventRepository;
import org.hdschools.timebank.repository.StuBalanceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for scheduled recalculation of student balances.
 * Runs every Monday at 00:00 to ensure balance accuracy.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceRecalculationService {

    private final EventRepository eventRepository;
    private final StuBalanceRepository stuBalanceRepository;

    private static final int INITIAL_POINTS = 0;
    private static final int INITIAL_CREDITS = 100;

    /**
     * Recalculates all student balances based on event history.
     * Scheduled to run every Monday at 00:00 (midnight).
     * <p>
     * Points start at 0 and only change with "accepted" type events.
     * Credits start at 100 and change with both "accepted" and "rejected" type events.
     */
    @Scheduled(cron = "0 0 0 * * MON")
    @Transactional
    public void recalculateAllBalances() {
        log.info("Starting scheduled balance recalculation...");
        
        try {
            // Calculate points from "accepted" events only
            Map<String, Integer> pointsByStudent = calculatePointsByStudent();
            
            // Calculate credits from "accepted" and "rejected" events
            Map<String, Integer> creditsByStudent = calculateCreditsByStudent();
            
            // Get all unique student user IDs
            Map<String, StuBalance> balancesToSave = new HashMap<>();
            
            // Process all students who have point changes
            for (Map.Entry<String, Integer> entry : pointsByStudent.entrySet()) {
                String userId = entry.getKey();
                int points = entry.getValue();
                int credits = creditsByStudent.getOrDefault(userId, 0) + INITIAL_CREDITS;
                
                StuBalance balance = stuBalanceRepository.findByUserId(userId)
                        .orElse(StuBalance.builder()
                                .userId(userId)
                                .accumulatedPoints(0)
                                .accumulatedCredits(0)
                                .build());
                
                balance.setAccumulatedPoints(INITIAL_POINTS + points);
                balance.setAccumulatedCredits(credits);
                balancesToSave.put(userId, balance);
            }
            
            // Process students who only have credit changes (rejected but no accepted)
            for (Map.Entry<String, Integer> entry : creditsByStudent.entrySet()) {
                String userId = entry.getKey();
                if (!balancesToSave.containsKey(userId)) {
                    int credits = entry.getValue() + INITIAL_CREDITS;
                    
                    StuBalance balance = stuBalanceRepository.findByUserId(userId)
                            .orElse(StuBalance.builder()
                                    .userId(userId)
                                    .accumulatedPoints(0)
                                    .accumulatedCredits(0)
                                    .build());
                    
                    balance.setAccumulatedPoints(INITIAL_POINTS);
                    balance.setAccumulatedCredits(credits);
                    balancesToSave.put(userId, balance);
                }
            }
            
            // Save all balances
            if (!balancesToSave.isEmpty()) {
                stuBalanceRepository.saveAll(balancesToSave.values());
                log.info("Balance recalculation completed successfully. Updated {} student balances.", 
                        balancesToSave.size());
            } else {
                log.info("No student balances to recalculate.");
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
}
