package com.biliqis.hafsahs_place.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for logging service method executions.
 * Logs entry, exit, and exceptions for all service layer methods.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Pointcut for all methods in service package
     */
    @Pointcut("execution(* com.biliqis.hafsahs_place.service..*(..))")
    public void serviceLayerMethods() {}

    /**
     * Pointcut for all methods in controller package
     */
    @Pointcut("execution(* com.biliqis.hafsahs_place.controller..*(..))")
    public void controllerLayerMethods() {}

    /**
     * Pointcut for all methods in repository package
     */
    @Pointcut("execution(* com.biliqis.hafsahs_place.repository..*(..))")
    public void repositoryLayerMethods() {}

    /**
     * Log method entry for service layer
     */
    @Before("serviceLayerMethods()")
    public void logBeforeServiceMethod(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        logger.info("==> [SERVICE] Entering: {}.{}() with arguments: {}",
                className, methodName, Arrays.toString(args));
    }

    /**
     * Log method exit for service layer
     */
    @AfterReturning(pointcut = "serviceLayerMethods()", returning = "result")
    public void logAfterServiceMethod(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        logger.info("<== [SERVICE] Exiting: {}.{}() with result: {}",
                className, methodName, result);
    }

    /**
     * Log exceptions from service layer
     */
    @AfterThrowing(pointcut = "serviceLayerMethods()", throwing = "exception")
    public void logAfterThrowingServiceMethod(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        logger.error("<!> [SERVICE] Exception in: {}.{}() with message: {}",
                className, methodName, exception.getMessage(), exception);
    }

    /**
     * Log controller method calls with concise format
     */
    @Before("controllerLayerMethods()")
    public void logBeforeControllerMethod(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        logger.info(">>> [CONTROLLER] {}.{}()", className, methodName);
    }

    /**
     * Log repository method calls (at DEBUG level to reduce noise)
     */
    @Before("repositoryLayerMethods()")
    public void logBeforeRepositoryMethod(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        logger.debug("--- [REPOSITORY] {}.{}()", className, methodName);
    }

    /**
     * Around advice for comprehensive logging with timing
     */
    @Around("serviceLayerMethods()")
    public Object logAroundServiceMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String className = proceedingJoinPoint.getSignature().getDeclaringTypeName();
        String methodName = proceedingJoinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();

        try {
            Object result = proceedingJoinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            logger.debug("[TIMING] {}.{}() executed in {} ms",
                    className, methodName, executionTime);

            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;

            logger.error("[TIMING] {}.{}() failed after {} ms",
                    className, methodName, executionTime);

            throw throwable;
        }
    }
}
