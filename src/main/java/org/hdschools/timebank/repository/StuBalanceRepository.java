package org.hdschools.timebank.repository;

import java.util.Optional;
import org.hdschools.timebank.model.StuBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StuBalanceRepository extends JpaRepository<StuBalance, Long> {
    
    /**
     * Finds a student balance record by user ID.
     *
     * @param userId the student user ID
     * @return Optional containing the balance if found
     */
    Optional<StuBalance> findByUserId(String userId);
}
