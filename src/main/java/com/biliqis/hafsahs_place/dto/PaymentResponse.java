package com.biliqis.hafsahs_place.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private String paymentReference;
    private String paymentMethod;
    private BigDecimal amount;
    private String status;
    private String authorizationUrl;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}
