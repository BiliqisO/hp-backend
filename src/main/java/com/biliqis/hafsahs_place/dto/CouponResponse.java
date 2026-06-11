package com.biliqis.hafsahs_place.dto;

import com.biliqis.hafsahs_place.model.Coupon;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CouponResponse {

    private Long id;
    private String code;
    private Coupon.CouponType type;
    private BigDecimal value;
    private BigDecimal minOrderAmount;
    private Integer maxUses;
    private int usedCount;
    private LocalDateTime expiresAt;
    private boolean active;
    private LocalDateTime createdAt;

    public static CouponResponse from(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .type(coupon.getType())
                .value(coupon.getValue())
                .minOrderAmount(coupon.getMinOrderAmount())
                .maxUses(coupon.getMaxUses())
                .usedCount(coupon.getUsedCount())
                .expiresAt(coupon.getExpiresAt())
                .active(Boolean.TRUE.equals(coupon.getIsActive()))
                .createdAt(coupon.getCreatedAt())
                .build();
    }
}
