package com.biliqis.hafsahs_place.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaystackService {

    @Value("${paystack.secret-key}")
    private String secretKey;

    @Value("${paystack.api-url}")
    private String apiUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> initializePayment(String email, BigDecimal amount, String reference) {
        String url = apiUrl + "/transaction/initialize";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + secretKey);

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        // Paystack expects amount in kobo (multiply by 100)
        body.put("amount", amount.multiply(BigDecimal.valueOf(100)).longValue());
        body.put("reference", reference);
        body.put("currency", "NGN");
        body.put("callback_url", frontendUrl + "/payment/verify");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        return response.getBody();
    }

    public Map<String, Object> verifyPayment(String reference) {
        String url = apiUrl + "/transaction/verify/" + reference;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + secretKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        return response.getBody();
    }

    /**
     * Verifies that a webhook payload came from Paystack by computing
     * HMAC-SHA512 of the raw body with the secret key and comparing it
     * to the X-Paystack-Signature header value.
     */
    public boolean verifyWebhookSignature(String payload, String signature) {
        if (signature == null || payload == null) return false;
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString().equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
}
