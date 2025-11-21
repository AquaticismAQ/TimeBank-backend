package org.hdschools.timebank.repository;

import org.hdschools.timebank.model.StuUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StuUserRepository extends JpaRepository<StuUser, Long> {
}
