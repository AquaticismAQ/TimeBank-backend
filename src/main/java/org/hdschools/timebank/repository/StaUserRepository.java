package org.hdschools.timebank.repository;

import java.util.Optional;
import org.hdschools.timebank.model.StaUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaUserRepository extends JpaRepository<StaUser, Long> {
    Optional<StaUser> findByUserId(String userId);
}
