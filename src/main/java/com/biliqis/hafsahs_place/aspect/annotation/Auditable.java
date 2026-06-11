package com.biliqis.hafsahs_place.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods that should be audited.
 * Use this on methods that perform sensitive operations.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    /**
     * Type of action being performed
     */
    String action() default "";

    /**
     * Description of the operation
     */
    String description() default "";

    /**
     * Criticality level
     */
    Level level() default Level.MEDIUM;

    enum Level {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}
