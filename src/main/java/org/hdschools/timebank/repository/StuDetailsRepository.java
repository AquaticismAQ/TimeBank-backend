package org.hdschools.timebank.repository;

import java.util.Optional;
import org.hdschools.timebank.model.StuDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StuDetailsRepository extends JpaRepository<StuDetails, Long> {
    
    /**
     * Finds a student details record by user ID.
     *
     * @param userId the student user ID
     * @return Optional containing the details if found
     */
    Optional<StuDetails> findByUserId(String userId);
}
