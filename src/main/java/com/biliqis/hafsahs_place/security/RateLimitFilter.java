package com.biliqis.hafsahs_place.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servlet filter that applies per-IP rate limits on sensitive auth endpoints.
 * Buckets are stored in memory — suitable for single-instance deployments.
 * For multi-instance, replace the ConcurrentHashMap with a distributed cache (e.g. Redis via Bucket4j's JCache integration).
 */
@Component
public class RateLimitFilter implements Filter {

    // Keyed by "ip:endpoint-key" → Bucket
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Endpoint keys and their bandwidth configurations
    private static final String LOGIN_KEY          = "login";
    private static final String REGISTER_KEY       = "register";
    private static final String FORGOT_PASSWORD_KEY = "forgot-password";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path   = request.getServletPath();
        String method = request.getMethod();

        if (!"POST".equalsIgnoreCase(method)) {
            chain.doFilter(req, res);
            return;
        }

        String endpointKey = resolveEndpointKey(path);
        if (endpointKey == null) {
            chain.doFilter(req, res);
            return;
        }

        String ip     = extractClientIp(request);
        String mapKey = ip + ":" + endpointKey;
        Bucket bucket = buckets.computeIfAbsent(mapKey, k -> buildBucket(endpointKey));

        if (bucket.tryConsume(1)) {
            chain.doFilter(req, res);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"status\":429,\"error\":\"Too Many Requests\","
                    + "\"message\":\"Rate limit exceeded. Please try again later.\"}"
            );
        }
    }

    private String resolveEndpointKey(String path) {
        if (path.equals("/api/auth/login"))            return LOGIN_KEY;
        if (path.equals("/api/auth/register"))         return REGISTER_KEY;
        if (path.equals("/api/auth/forgot-password"))  return FORGOT_PASSWORD_KEY;
        return null;
    }

    private Bucket buildBucket(String endpointKey) {
        Bandwidth limit = switch (endpointKey) {
            case LOGIN_KEY ->
                // 10 attempts per 15 minutes
                Bandwidth.builder()
                        .capacity(10)
                        .refillGreedy(10, Duration.ofMinutes(15))
                        .build();
            case FORGOT_PASSWORD_KEY ->
                // 5 attempts per hour
                Bandwidth.builder()
                        .capacity(5)
                        .refillGreedy(5, Duration.ofHours(1))
                        .build();
            default ->
                // register: 20 attempts per hour
                Bandwidth.builder()
                        .capacity(20)
                        .refillGreedy(20, Duration.ofHours(1))
                        .build();
        };
        return Bucket.builder().addLimit(limit).build();
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            // X-Forwarded-For may contain a comma-separated list; the first entry is the originating client
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
