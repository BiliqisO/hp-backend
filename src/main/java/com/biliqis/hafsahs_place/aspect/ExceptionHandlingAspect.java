package com.biliqis.hafsahs_place.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Aspect for centralized exception handling and logging.
 * Captures and logs all exceptions thrown from various layers.
 */
@Aspect
@Component
public class ExceptionHandlingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlingAspect.class);
    private static final Logger errorLogger = LoggerFactory.getLogger("ERROR_LOG");

    /**
     * Pointcut for all methods in the application
     */
    @Pointcut("within(com.biliqis.hafsahs_place..*)")
    public void applicationMethods() {}

    /**
     * Pointcut for controller layer
     */
    @Pointcut("execution(* com.biliqis.hafsahs_place.controller..*(..))")
    public void controllerMethods() {}

    /**
     * Pointcut for service layer
     */
    @Pointcut("execution(* com.biliqis.hafsahs_place.service..*(..))")
    public void serviceMethods() {}

    /**
     * Pointcut for repository layer
     */
    @Pointcut("execution(* com.biliqis.hafsahs_place.repository..*(..))")
    public void repositoryMethods() {}

    /**
     * Handle all exceptions from controller layer
     */
    @AfterThrowing(pointcut = "controllerMethods()", throwing = "exception")
    public void handleControllerException(JoinPoint joinPoint, Throwable exception) {
        logException("CONTROLLER", joinPoint, exception);
    }

    /**
     * Handle all exceptions from service layer
     */
    @AfterThrowing(pointcut = "serviceMethods()", throwing = "exception")
    public void handleServiceException(JoinPoint joinPoint, Throwable exception) {
        logException("SERVICE", joinPoint, exception);
    }

    /**
     * Handle all exceptions from repository layer
     */
    @AfterThrowing(pointcut = "repositoryMethods()", throwing = "exception")
    public void handleRepositoryException(JoinPoint joinPoint, Throwable exception) {
        logException("REPOSITORY", joinPoint, exception);
    }

    /**
     * Common method to log exceptions with detailed information
     */
    private void logException(String layer, JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String timestamp = LocalDateTime.now().toString();

        // Log to standard logger
        logger.error("[{}_EXCEPTION] Class: {}, Method: {}, Args: {}, Exception: {}",
                layer, className, methodName, Arrays.toString(args), exception.getMessage());

        // Log detailed error to separate error log file
        errorLogger.error("=== Exception Details ===");
        errorLogger.error("Timestamp: {}", timestamp);
        errorLogger.error("Layer: {}", layer);
        errorLogger.error("Class: {}", className);
        errorLogger.error("Method: {}", methodName);
        errorLogger.error("Arguments: {}", Arrays.toString(args));
        errorLogger.error("Exception Type: {}", exception.getClass().getName());
        errorLogger.error("Exception Message: {}", exception.getMessage());
        errorLogger.error("Stack Trace:", exception);
        errorLogger.error("========================\n");

        // Additional handling based on exception type
        if (exception instanceof NullPointerException) {
            logger.error("[NULL_POINTER] Potential null value in {}.{}", className, methodName);
        } else if (exception instanceof IllegalArgumentException) {
            logger.error("[ILLEGAL_ARGUMENT] Invalid argument in {}.{}: {}",
                    className, methodName, exception.getMessage());
        } else if (exception instanceof RuntimeException) {
            logger.error("[RUNTIME_EXCEPTION] Runtime error in {}.{}: {}",
                    className, methodName, exception.getMessage());
        }
    }

    /**
     * Handle specific database-related exceptions
     */
    @AfterThrowing(pointcut = "repositoryMethods()", throwing = "exception")
    public void handleDatabaseException(JoinPoint joinPoint, Exception exception) {
        String methodName = joinPoint.getSignature().toShortString();

        if (exception.getMessage() != null) {
            if (exception.getMessage().contains("constraint")) {
                logger.error("[DB_CONSTRAINT_VIOLATION] Method: {}, Error: {}",
                        methodName, exception.getMessage());
            } else if (exception.getMessage().contains("duplicate")) {
                logger.error("[DB_DUPLICATE_ENTRY] Method: {}, Error: {}",
                        methodName, exception.getMessage());
            } else if (exception.getMessage().contains("timeout")) {
                logger.error("[DB_TIMEOUT] Method: {}, Error: {}",
                        methodName, exception.getMessage());
            }
        }
    }
}
