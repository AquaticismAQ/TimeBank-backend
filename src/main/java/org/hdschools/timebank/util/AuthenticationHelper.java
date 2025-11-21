package org.hdschools.timebank.util;

import jakarta.servlet.http.HttpServletRequest;
import org.hdschools.timebank.config.AuthenticationInterceptor;
import org.hdschools.timebank.model.Token;

/**
 * Helper utility for accessing authenticated user information in controllers.
 */
public class AuthenticationHelper {

    /**
     * Retrieves the authenticated user's ID from the request.
     *
     * @param request the HTTP request
     * @return the authenticated user's ID, or null if not authenticated
     */
    public static Long getAuthenticatedUserId(HttpServletRequest request) {
        return (Long) request.getAttribute(AuthenticationInterceptor.USER_ID_ATTRIBUTE);
    }

    /**
     * Retrieves the authenticated user's type from the request.
     *
     * @param request the HTTP request
     * @return the authenticated user's type ("student" or "staff"), or null if not authenticated
     */
    public static String getAuthenticatedUserType(HttpServletRequest request) {
        return (String) request.getAttribute(AuthenticationInterceptor.USER_TYPE_ATTRIBUTE);
    }

    /**
     * Retrieves the full Token object from the request.
     *
     * @param request the HTTP request
     * @return the Token object, or null if not authenticated
     */
    public static Token getAuthenticatedToken(HttpServletRequest request) {
        return (Token) request.getAttribute(AuthenticationInterceptor.TOKEN_ATTRIBUTE);
    }
}
