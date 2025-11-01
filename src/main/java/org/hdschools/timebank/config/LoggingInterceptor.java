package org.hdschools.timebank.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Intercepts every HTTP request to log incoming and completion details.
 */
@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    /**
     * Logs details about every incoming HTTP request before it reaches a controller.
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute
     * @return {@code true} to allow request processing to continue.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("Incoming request: method={}, uri={}, remoteAddr={}, params={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                request.getQueryString());
        return true;
    }

    /**
     * Logs completion details after the request has been processed by the controller.
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute
     * @param ex       optional exception thrown during execution
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.info("Completed request: method={}, uri={}, status={}, exception={}",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                ex != null ? ex.getMessage() : "none");
    }
}
