package com.biliqis.hafsahs_place.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable performance tracking for specific methods.
 * Logs execution time and can alert on slow methods.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackPerformance {

    /**
     * Warning threshold in milliseconds
     */
    long warnThreshold() default 1000;

    /**
     * Whether to log parameters
     */
    boolean logParams() default false;

    /**
     * Description of what is being tracked
     */
    String description() default "";
}
