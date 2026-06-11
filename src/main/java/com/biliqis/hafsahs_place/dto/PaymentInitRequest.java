package com.biliqis.hafsahs_place.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    private String paymentMethod;
}
