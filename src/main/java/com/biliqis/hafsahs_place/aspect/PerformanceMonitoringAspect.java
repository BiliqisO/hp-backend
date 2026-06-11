package com.biliqis.hafsahs_place.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Aspect for monitoring application performance.
 * Tracks execution time and method call statistics.
 */
@Aspect
@Component
public class PerformanceMonitoringAspect {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitoringAspect.class);

    // Store method execution statistics
    private final ConcurrentHashMap<String, MethodStats> methodStatsMap = new ConcurrentHashMap<>();

    /**
     * Pointcut for all service methods
     */
    @Pointcut("execution(* com.biliqis.hafsahs_place.service..*(..))")
    public void serviceMethods() {}

    /**
     * Pointcut for all controller methods
     */
    @Pointcut("execution(* com.biliqis.hafsahs_place.controller..*(..))")
    public void controllerMethods() {}

    /**
     * Monitor performance of service methods
     */
    @Around("serviceMethods()")
    public Object monitorServicePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorMethodPerformance(joinPoint, "SERVICE");
    }

    /**
     * Monitor performance of controller methods
     */
    @Around("controllerMethods()")
    public Object monitorControllerPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorMethodPerformance(joinPoint, "CONTROLLER");
    }

    /**
     * Common method to monitor performance
     */
    private Object monitorMethodPerformance(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            // Update statistics
            updateMethodStats(methodName, executionTime, true);

            // Log if execution time exceeds threshold
            if (executionTime > 1000) { // 1 second threshold
                logger.warn("[PERFORMANCE] Slow {} method: {} took {} ms",
                        layer, methodName, executionTime);
            } else if (executionTime > 500) { // 500ms threshold
                logger.info("[PERFORMANCE] {} method: {} took {} ms",
                        layer, methodName, executionTime);
            } else {
                logger.debug("[PERFORMANCE] {} method: {} took {} ms",
                        layer, methodName, executionTime);
            }

            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            updateMethodStats(methodName, executionTime, false);

            logger.error("[PERFORMANCE] {} method failed: {} after {} ms",
                    layer, methodName, executionTime);

            throw throwable;
        }
    }

    /**
     * Update statistics for a method
     */
    private void updateMethodStats(String methodName, long executionTime, boolean success) {
        methodStatsMap.compute(methodName, (key, stats) -> {
            if (stats == null) {
                stats = new MethodStats();
            }
            stats.recordExecution(executionTime, success);
            return stats;
        });
    }

    /**
     * Get statistics for a specific method
     */
    public MethodStats getMethodStats(String methodName) {
        return methodStatsMap.get(methodName);
    }

    /**
     * Get all method statistics
     */
    public ConcurrentHashMap<String, MethodStats> getAllStats() {
        return new ConcurrentHashMap<>(methodStatsMap);
    }

    /**
     * Clear all statistics
     */
    public void clearStats() {
        methodStatsMap.clear();
        logger.info("[PERFORMANCE] Statistics cleared");
    }

    /**
     * Inner class to hold method execution statistics
     */
    public static class MethodStats {
        private final AtomicLong callCount = new AtomicLong(0);
        private final AtomicLong successCount = new AtomicLong(0);
        private final AtomicLong failureCount = new AtomicLong(0);
        private final AtomicLong totalExecutionTime = new AtomicLong(0);
        private volatile long minExecutionTime = Long.MAX_VALUE;
        private volatile long maxExecutionTime = Long.MIN_VALUE;

        public void recordExecution(long executionTime, boolean success) {
            callCount.incrementAndGet();
            totalExecutionTime.addAndGet(executionTime);

            if (success) {
                successCount.incrementAndGet();
            } else {
                failureCount.incrementAndGet();
            }

            // Update min/max
            if (executionTime < minExecutionTime) {
                minExecutionTime = executionTime;
            }
            if (executionTime > maxExecutionTime) {
                maxExecutionTime = executionTime;
            }
        }

        public long getCallCount() {
            return callCount.get();
        }

        public long getSuccessCount() {
            return successCount.get();
        }

        public long getFailureCount() {
            return failureCount.get();
        }

        public long getAverageExecutionTime() {
            long count = callCount.get();
            return count > 0 ? totalExecutionTime.get() / count : 0;
        }

        public long getMinExecutionTime() {
            return minExecutionTime == Long.MAX_VALUE ? 0 : minExecutionTime;
        }

        public long getMaxExecutionTime() {
            return maxExecutionTime == Long.MIN_VALUE ? 0 : maxExecutionTime;
        }

        @Override
        public String toString() {
            return String.format(
                    "MethodStats{calls=%d, success=%d, failure=%d, avg=%dms, min=%dms, max=%dms}",
                    getCallCount(), getSuccessCount(), getFailureCount(),
                    getAverageExecutionTime(), getMinExecutionTime(), getMaxExecutionTime()
            );
        }
    }
}
