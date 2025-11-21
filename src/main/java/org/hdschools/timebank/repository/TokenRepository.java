package org.hdschools.timebank.repository;

import java.util.Optional;
import org.hdschools.timebank.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    void deleteByUserId(Long userId);
}
