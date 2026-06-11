package com.biliqis.hafsahs_place.aspect;

import com.biliqis.hafsahs_place.aspect.annotation.Auditable;
import com.biliqis.hafsahs_place.aspect.annotation.TrackPerformance;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Aspect for handling custom annotations (@Auditable, @TrackPerformance).
 */
@Aspect
@Component
public class CustomAnnotationAspect {

    private static final Logger logger = LoggerFactory.getLogger(CustomAnnotationAspect.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOG");
    private static final Logger performanceLogger = LoggerFactory.getLogger("PERFORMANCE_LOG");

    /**
     * Handle methods annotated with @Auditable
     */
    @Around("@annotation(auditable)")
    public Object handleAuditable(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        String currentUser = getCurrentUsername();
        String methodName = joinPoint.getSignature().toShortString();
        String action = auditable.action().isEmpty() ? methodName : auditable.action();
        String description = auditable.description();
        Auditable.Level level = auditable.level();
        String timestamp = LocalDateTime.now().toString();

        // Log before execution
        auditLogger.info("[AUDIT] [{}] Timestamp: {} | User: {} | Action: {} | Description: {} | Status: STARTED",
                level, timestamp, currentUser, action, description);

        Object result = null;
        try {
            result = joinPoint.proceed();

            // Log success
            auditLogger.info("[AUDIT] [{}] Timestamp: {} | User: {} | Action: {} | Status: SUCCESS",
                    level, LocalDateTime.now(), currentUser, action);

            return result;
        } catch (Throwable throwable) {
            // Log failure
            auditLogger.error("[AUDIT] [{}] Timestamp: {} | User: {} | Action: {} | Status: FAILED | Error: {}",
                    level, LocalDateTime.now(), currentUser, action, throwable.getMessage());
            throw throwable;
        }
    }

    /**
     * Handle methods annotated with @TrackPerformance
     */
    @Around("@annotation(trackPerformance)")
    public Object handleTrackPerformance(ProceedingJoinPoint joinPoint, TrackPerformance trackPerformance) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.toShortString();
        long warnThreshold = trackPerformance.warnThreshold();
        boolean logParams = trackPerformance.logParams();
        String description = trackPerformance.description();

        long startTime = System.currentTimeMillis();

        // Log parameters if enabled
        if (logParams) {
            performanceLogger.debug("[TRACK] Starting: {} | Params: {} | Description: {}",
                    methodName, Arrays.toString(joinPoint.getArgs()), description);
        } else {
            performanceLogger.debug("[TRACK] Starting: {} | Description: {}",
                    methodName, description);
        }

        Object result = null;
        try {
            result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            // Log based on execution time
            if (executionTime > warnThreshold) {
                performanceLogger.warn("[TRACK] [SLOW] Method: {} | Execution Time: {}ms | Threshold: {}ms | Description: {}",
                        methodName, executionTime, warnThreshold, description);
            } else {
                performanceLogger.info("[TRACK] Method: {} | Execution Time: {}ms | Description: {}",
                        methodName, executionTime, description);
            }

            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            performanceLogger.error("[TRACK] [FAILED] Method: {} | Execution Time: {}ms | Error: {}",
                    methodName, executionTime, throwable.getMessage());
            throw throwable;
        }
    }

    /**
     * Get current authenticated username
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
            && !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName();
        }
        return "Anonymous";
    }
}
