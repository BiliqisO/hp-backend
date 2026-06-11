package com.biliqis.hafsahs_place.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Aspect for auditing sensitive operations.
 * Logs who did what and when for security and compliance purposes.
 */
@Aspect
@Component
public class AuditLoggingAspect {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOG");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Pointcut for authentication-related methods
     */
    @Pointcut("execution(* com.biliqis.hafsahs_place.service.AuthService.*(..))")
    public void authenticationMethods() {}

    /**
     * Pointcut for user registration and login
     */
    @Pointcut("execution(* com.biliqis.hafsahs_place.service.AuthService.register(..)) || " +
              "execution(* com.biliqis.hafsahs_place.service.AuthService.login(..))")
    public void authenticationActions() {}

    /**
     * Pointcut for order creation and updates
     */
    @Pointcut("execution(* com.biliqis.hafsahs_place.service.*Service.createOrder(..)) || " +
              "execution(* com.biliqis.hafsahs_place.service.*Service.updateOrder(..))")
    public void orderOperations() {}

    /**
     * Pointcut for payment operations
     */
    @Pointcut("execution(* com.biliqis.hafsahs_place.service.PaystackService.*(..))")
    public void paymentOperations() {}

    /**
     * Pointcut for product creation, update, and deletion (admin operations)
     */
    @Pointcut("execution(* com.biliqis.hafsahs_place.service.ProductService.createProduct(..)) || " +
              "execution(* com.biliqis.hafsahs_place.service.ProductService.updateProduct(..)) || " +
              "execution(* com.biliqis.hafsahs_place.service.ProductService.deleteProduct(..))")
    public void productAdminOperations() {}

    /**
     * Audit successful authentication
     */
    @AfterReturning(pointcut = "authenticationActions()", returning = "result")
    public void auditAuthenticationSuccess(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        String email = extractEmailFromArgs(args);
        String timestamp = LocalDateTime.now().format(formatter);

        auditLogger.info("[AUTH_SUCCESS] Timestamp: {} | Action: {} | User: {} | Result: Success",
                timestamp, methodName, email);
    }

    /**
     * Audit failed authentication
     */
    @AfterThrowing(pointcut = "authenticationActions()", throwing = "exception")
    public void auditAuthenticationFailure(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        String email = extractEmailFromArgs(args);
        String timestamp = LocalDateTime.now().format(formatter);

        auditLogger.warn("[AUTH_FAILURE] Timestamp: {} | Action: {} | User: {} | Reason: {}",
                timestamp, methodName, email, exception.getMessage());
    }

    /**
     * Audit order operations
     */
    @Before("orderOperations()")
    public void auditOrderOperation(JoinPoint joinPoint) {
        String currentUser = getCurrentUsername();
        String methodName = joinPoint.getSignature().getName();
        String timestamp = LocalDateTime.now().format(formatter);

        auditLogger.info("[ORDER_OPERATION] Timestamp: {} | Action: {} | User: {} | Args: {}",
                timestamp, methodName, currentUser, Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * Audit payment operations
     */
    @Before("paymentOperations()")
    public void auditPaymentOperation(JoinPoint joinPoint) {
        String currentUser = getCurrentUsername();
        String methodName = joinPoint.getSignature().getName();
        String timestamp = LocalDateTime.now().format(formatter);

        auditLogger.info("[PAYMENT_OPERATION] Timestamp: {} | Action: {} | User: {}",
                timestamp, methodName, currentUser);
    }

    /**
     * Audit payment success
     */
    @AfterReturning(pointcut = "paymentOperations()", returning = "result")
    public void auditPaymentSuccess(JoinPoint joinPoint, Object result) {
        String currentUser = getCurrentUsername();
        String methodName = joinPoint.getSignature().getName();
        String timestamp = LocalDateTime.now().format(formatter);

        auditLogger.info("[PAYMENT_SUCCESS] Timestamp: {} | Action: {} | User: {} | Result: {}",
                timestamp, methodName, currentUser, result);
    }

    /**
     * Audit payment failure
     */
    @AfterThrowing(pointcut = "paymentOperations()", throwing = "exception")
    public void auditPaymentFailure(JoinPoint joinPoint, Throwable exception) {
        String currentUser = getCurrentUsername();
        String methodName = joinPoint.getSignature().getName();
        String timestamp = LocalDateTime.now().format(formatter);

        auditLogger.error("[PAYMENT_FAILURE] Timestamp: {} | Action: {} | User: {} | Error: {}",
                timestamp, methodName, currentUser, exception.getMessage());
    }

    /**
     * Audit admin product operations
     */
    @Before("productAdminOperations()")
    public void auditProductAdminOperation(JoinPoint joinPoint) {
        String currentUser = getCurrentUsername();
        String methodName = joinPoint.getSignature().getName();
        String timestamp = LocalDateTime.now().format(formatter);

        auditLogger.info("[ADMIN_OPERATION] Timestamp: {} | Action: {} | Admin: {} | Target: Product",
                timestamp, methodName, currentUser);
    }

    /**
     * Audit successful admin operations
     */
    @AfterReturning(pointcut = "productAdminOperations()", returning = "result")
    public void auditProductAdminOperationSuccess(JoinPoint joinPoint, Object result) {
        String currentUser = getCurrentUsername();
        String methodName = joinPoint.getSignature().getName();
        String timestamp = LocalDateTime.now().format(formatter);

        auditLogger.info("[ADMIN_SUCCESS] Timestamp: {} | Action: {} | Admin: {} | Result: Success",
                timestamp, methodName, currentUser);
    }

    /**
     * Get current authenticated username
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "Anonymous";
    }

    /**
     * Extract email from method arguments
     */
    private String extractEmailFromArgs(Object[] args) {
        if (args != null && args.length > 0) {
            Object firstArg = args[0];
            try {
                // Try to get email field using reflection
                return firstArg.getClass().getMethod("getEmail").invoke(firstArg).toString();
            } catch (Exception e) {
                return "Unknown";
            }
        }
        return "Unknown";
    }
}
