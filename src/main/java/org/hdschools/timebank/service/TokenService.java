package org.hdschools.timebank.service;

import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hdschools.timebank.model.Token;
import org.hdschools.timebank.repository.TokenRepository;
import org.springframework.stereotype.Service;

/**
 * Manages authentication tokens with rolling 30-minute expiration.
 */
@Service
@Slf4j
public class TokenService {

    private static final int TOKEN_EXPIRY_MINUTES = 30;
    private static final int TOKEN_LENGTH_BYTES = 32;
    private final TokenRepository tokenRepository;
    private final SecureRandom secureRandom;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        this.secureRandom = new SecureRandom();
    }

    /**
     * Generates a new authentication token for a user.
     *
     * @param userId   the user's ID
     * @param userType the type of user ("student" or "staff")
     * @return the generated token string
     */
    @Transactional
    public String generateToken(Long userId, String userType) {
        // Delete any existing tokens for this user
        tokenRepository.deleteByUserId(userId);

        // Generate secure random token
        byte[] randomBytes = new byte[TOKEN_LENGTH_BYTES];
        secureRandom.nextBytes(randomBytes);
        String tokenString = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        // Create and save token with 30-minute expiration
        Token token = Token.builder()
                .token(tokenString)
                .userId(userId)
                .userType(userType)
                .expiresAt(Instant.now().plus(TOKEN_EXPIRY_MINUTES, ChronoUnit.MINUTES))
                .build();

        tokenRepository.save(token);
        log.info("Generated token for userId={}, userType={}", userId, userType);

        return tokenString;
    }

    /**
     * Validates a token and returns the associated Token entity if valid.
     * Does NOT refresh expiration - use {@link #validateAndRefreshToken} for that.
     *
     * @param tokenString the token to validate
     * @return Optional containing the Token if valid and not expired
     */
    public Optional<Token> validateToken(String tokenString) {
        return tokenRepository.findByToken(tokenString)
                .filter(token -> token.getExpiresAt().isAfter(Instant.now()));
    }

    /**
     * Validates a token and refreshes its expiration to 30 minutes from now.
     * This implements rolling expiration for authenticated requests.
     *
     * @param tokenString the token to validate and refresh
     * @return Optional containing the Token if valid
     */
    @Transactional
    public Optional<Token> validateAndRefreshToken(String tokenString) {
        Optional<Token> tokenOpt = validateToken(tokenString);
        
        if (tokenOpt.isPresent()) {
            Token token = tokenOpt.get();
            token.setExpiresAt(Instant.now().plus(TOKEN_EXPIRY_MINUTES, ChronoUnit.MINUTES));
            tokenRepository.save(token);
            log.debug("Refreshed token expiration for userId={}", token.getUserId());
        }
        
        return tokenOpt;
    }

    /**
     * Invalidates a token by deleting it.
     *
     * @param tokenString the token to invalidate
     */
    @Transactional
    public void invalidateToken(String tokenString) {
        tokenRepository.findByToken(tokenString)
                .ifPresent(token -> {
                    tokenRepository.delete(token);
                    log.info("Invalidated token for userId={}", token.getUserId());
                });
    }
}
