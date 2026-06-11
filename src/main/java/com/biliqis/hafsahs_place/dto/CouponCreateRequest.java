package com.biliqis.hafsahs_place.dto;

import com.biliqis.hafsahs_place.model.Coupon;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponCreateRequest {

    @NotBlank(message = "Coupon code is required")
    private String code;

    @NotNull(message = "Coupon type is required")
    private Coupon.CouponType type;

    @NotNull
    @Positive(message = "Value must be positive")
    private BigDecimal value;

    private BigDecimal minOrderAmount;

    private Integer maxUses;

    private LocalDateTime expiresAt;
}
