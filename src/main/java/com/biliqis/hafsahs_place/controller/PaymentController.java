package com.biliqis.hafsahs_place.controller;

import com.biliqis.hafsahs_place.dto.PaymentInitRequest;
import com.biliqis.hafsahs_place.dto.PaymentResponse;
import com.biliqis.hafsahs_place.service.PaymentService;
import com.biliqis.hafsahs_place.service.PaystackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Payments", description = "Initialize and verify Paystack payments")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaystackService paystackService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/initialize")
    public ResponseEntity<PaymentResponse> initializePayment(
            Authentication authentication,
            @Valid @RequestBody PaymentInitRequest request) {
        PaymentResponse response = paymentService.initializePayment(
                request.getOrderId(), authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/verify/{reference}")
    public ResponseEntity<PaymentResponse> verifyPayment(@PathVariable String reference) {
        return ResponseEntity.ok(paymentService.verifyPayment(reference));
    }

    @GetMapping("/{reference}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String reference) {
        return ResponseEntity.ok(paymentService.getPaymentByReference(reference));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestHeader(value = "X-Paystack-Signature", required = false) String signature,
            @RequestBody String payload) {

        if (!paystackService.verifyWebhookSignature(payload, signature)) {
            log.warn("Paystack webhook received with invalid signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = objectMapper.readValue(payload, Map.class);
            String eventType = (String) event.get("event");
            log.info("Paystack webhook received: event={}", eventType);

            if ("charge.success".equals(eventType)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) event.get("data");
                String reference = (String) data.get("reference");
                paymentService.verifyPayment(reference);
                log.info("Payment verified via webhook: reference={}", reference);
            }
        } catch (Exception e) {
            // Always return 200 so Paystack does not retry endlessly
            log.error("Error processing Paystack webhook payload", e);
        }

        return ResponseEntity.ok("OK");
    }
}
