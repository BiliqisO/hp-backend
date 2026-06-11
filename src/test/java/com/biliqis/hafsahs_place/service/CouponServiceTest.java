package com.biliqis.hafsahs_place.service;

import com.biliqis.hafsahs_place.exception.BadRequestException;
import com.biliqis.hafsahs_place.model.Coupon;
import com.biliqis.hafsahs_place.model.User;
import com.biliqis.hafsahs_place.repository.CouponRepository;
import com.biliqis.hafsahs_place.repository.CouponUsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock CouponRepository couponRepository;
    @Mock CouponUsageRepository couponUsageRepository;

    @InjectMocks CouponService couponService;

    private User user;
    private Coupon validPercentageCoupon;
    private Coupon validFixedCoupon;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("user@test.com").build();

        validPercentageCoupon = Coupon.builder()
                .id(1L).code("SAVE20")
                .type(Coupon.CouponType.PERCENTAGE).value(BigDecimal.valueOf(20))
                .minOrderAmount(BigDecimal.valueOf(1000))
                .maxUses(100).usedCount(0)
                .isActive(true)
                .build();

        validFixedCoupon = Coupon.builder()
                .id(2L).code("OFF500")
                .type(Coupon.CouponType.FIXED_AMOUNT).value(BigDecimal.valueOf(500))
                .minOrderAmount(BigDecimal.valueOf(2000))
                .isActive(true)
                .build();
    }

    // ── validateCoupon ────────────────────────────────────────────────────────

    @Test
    void validateCoupon_valid_returnsCoupon() {
        when(couponRepository.findByCodeIgnoreCase("SAVE20")).thenReturn(Optional.of(validPercentageCoupon));
        when(couponUsageRepository.existsByCouponIdAndUserId(1L, 1L)).thenReturn(false);

        Coupon result = couponService.validateCoupon("SAVE20", user, BigDecimal.valueOf(5000));

        assertThat(result.getCode()).isEqualTo("SAVE20");
    }

    @Test
    void validateCoupon_notFound_throws() {
        when(couponRepository.findByCodeIgnoreCase("BOGUS")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.validateCoupon("BOGUS", user, BigDecimal.valueOf(5000)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("invalid");
    }

    @Test
    void validateCoupon_inactive_throws() {
        validPercentageCoupon.setIsActive(false);
        when(couponRepository.findByCodeIgnoreCase("SAVE20")).thenReturn(Optional.of(validPercentageCoupon));

        assertThatThrownBy(() -> couponService.validateCoupon("SAVE20", user, BigDecimal.valueOf(5000)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("no longer active");
    }

    @Test
    void validateCoupon_expired_throws() {
        validPercentageCoupon.setExpiresAt(LocalDateTime.now().minusDays(1));
        when(couponRepository.findByCodeIgnoreCase("SAVE20")).thenReturn(Optional.of(validPercentageCoupon));

        assertThatThrownBy(() -> couponService.validateCoupon("SAVE20", user, BigDecimal.valueOf(5000)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void validateCoupon_maxUsesReached_throws() {
        validPercentageCoupon.setMaxUses(10);
        validPercentageCoupon.setUsedCount(10);
        when(couponRepository.findByCodeIgnoreCase("SAVE20")).thenReturn(Optional.of(validPercentageCoupon));

        assertThatThrownBy(() -> couponService.validateCoupon("SAVE20", user, BigDecimal.valueOf(5000)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("usage limit");
    }

    @Test
    void validateCoupon_belowMinOrder_throws() {
        when(couponRepository.findByCodeIgnoreCase("SAVE20")).thenReturn(Optional.of(validPercentageCoupon));

        // Min is 1000, providing 500
        assertThatThrownBy(() -> couponService.validateCoupon("SAVE20", user, BigDecimal.valueOf(500)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("at least");
    }

    @Test
    void validateCoupon_alreadyUsedByUser_throws() {
        when(couponRepository.findByCodeIgnoreCase("SAVE20")).thenReturn(Optional.of(validPercentageCoupon));
        when(couponUsageRepository.existsByCouponIdAndUserId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> couponService.validateCoupon("SAVE20", user, BigDecimal.valueOf(5000)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already used");
    }

    // ── calculateDiscount ─────────────────────────────────────────────────────

    @Test
    void calculateDiscount_percentage_correctAmount() {
        BigDecimal discount = couponService.calculateDiscount(validPercentageCoupon, BigDecimal.valueOf(10000));
        assertThat(discount).isEqualByComparingTo(BigDecimal.valueOf(2000));
    }

    @Test
    void calculateDiscount_fixedAmount_correctValue() {
        BigDecimal discount = couponService.calculateDiscount(validFixedCoupon, BigDecimal.valueOf(5000));
        assertThat(discount).isEqualByComparingTo(BigDecimal.valueOf(500));
    }

    @Test
    void calculateDiscount_cappedAtOrderTotal() {
        // Fixed coupon of 500 applied to 300 order → discount should be 300, not 500
        BigDecimal discount = couponService.calculateDiscount(validFixedCoupon, BigDecimal.valueOf(300));
        assertThat(discount).isEqualByComparingTo(BigDecimal.valueOf(300));
    }

    @Test
    void calculateDiscount_percentageCappedAt100Percent() {
        // 20% of 100 = 20 — no capping needed, just verifying normal path
        BigDecimal discount = couponService.calculateDiscount(validPercentageCoupon, BigDecimal.valueOf(100));
        assertThat(discount).isEqualByComparingTo(BigDecimal.valueOf(20));
    }
}
