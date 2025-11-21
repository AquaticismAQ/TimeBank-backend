package org.hdschools.timebank.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hdschools.timebank.model.Token;
import org.hdschools.timebank.service.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Intercepts requests to validate authentication tokens and refresh their expiration.
 * Implements rolling 30-minute token expiration on each successful request.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public static final String TOKEN_ATTRIBUTE = "authenticatedToken";
    public static final String USER_ID_ATTRIBUTE = "authenticatedUserId";
    public static final String USER_TYPE_ATTRIBUTE = "authenticatedUserType";

    /**
     * Validates the authentication token and refreshes its expiration.
     * If token is invalid or expired, returns 401 Unauthorized.
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute
     * @return {@code true} if token is valid, {@code false} otherwise
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        String path = request.getRequestURI();
        
        // Skip authentication for login endpoints and health check
        if (path.endsWith("/login") || path.endsWith("/health")) {
            return true;
        }

        // Extract token from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path={}", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Authentication required\",\"data\":null}");
            return false;
        }

        String tokenString = authHeader.substring(7); // Remove "Bearer " prefix

        // Validate and refresh token
        Optional<Token> tokenOpt = tokenService.validateAndRefreshToken(tokenString);
        
        if (tokenOpt.isEmpty()) {
            log.warn("Invalid or expired token for path={}", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Invalid or expired token\",\"data\":null}");
            return false;
        }

        // Store token information in request attributes for controllers
        Token token = tokenOpt.get();
        request.setAttribute(TOKEN_ATTRIBUTE, token);
        request.setAttribute(USER_ID_ATTRIBUTE, token.getUserId());
        request.setAttribute(USER_TYPE_ATTRIBUTE, token.getUserType());
        
        log.debug("Authenticated request: userId={}, userType={}, path={}", 
                token.getUserId(), token.getUserType(), path);

        return true;
    }
}
