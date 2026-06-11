package com.biliqis.hafsahs_place.service;

import com.biliqis.hafsahs_place.dto.CouponCreateRequest;
import com.biliqis.hafsahs_place.dto.CouponResponse;
import com.biliqis.hafsahs_place.exception.BadRequestException;
import com.biliqis.hafsahs_place.exception.ResourceNotFoundException;
import com.biliqis.hafsahs_place.model.Coupon;
import com.biliqis.hafsahs_place.model.CouponUsage;
import com.biliqis.hafsahs_place.model.Order;
import com.biliqis.hafsahs_place.model.User;
import com.biliqis.hafsahs_place.repository.CouponRepository;
import com.biliqis.hafsahs_place.repository.CouponUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponUsageRepository couponUsageRepository;

    /**
     * Validates a coupon code for a given user and order total.
     * Throws BadRequestException if the coupon is invalid for any reason.
     */
    public Coupon validateCoupon(String code, User user, BigDecimal orderTotal) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new BadRequestException("Coupon code '" + code + "' is invalid"));

        if (!Boolean.TRUE.equals(coupon.getIsActive())) {
            throw new BadRequestException("Coupon '" + code + "' is no longer active");
        }

        if (coupon.getExpiresAt() != null && LocalDateTime.now().isAfter(coupon.getExpiresAt())) {
            throw new BadRequestException("Coupon '" + code + "' has expired");
        }

        if (coupon.getMaxUses() != null && coupon.getUsedCount() >= coupon.getMaxUses()) {
            throw new BadRequestException("Coupon '" + code + "' has reached its usage limit");
        }

        if (orderTotal.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new BadRequestException("Order total must be at least ₦" + coupon.getMinOrderAmount()
                    + " to use this coupon");
        }

        if (couponUsageRepository.existsByCouponIdAndUserId(coupon.getId(), user.getId())) {
            throw new BadRequestException("You have already used coupon '" + code + "'");
        }

        return coupon;
    }

    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderTotal) {
        BigDecimal discount;
        if (coupon.getType() == Coupon.CouponType.PERCENTAGE) {
            discount = orderTotal.multiply(coupon.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            discount = coupon.getValue();
        }
        // Discount can never exceed the order total
        return discount.min(orderTotal);
    }

    @Transactional
    public void recordUsage(Coupon coupon, User user, Order order) {
        couponUsageRepository.save(CouponUsage.builder()
                .coupon(coupon)
                .user(user)
                .order(order)
                .build());
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);
    }

    // ── Admin operations ─────────────────────────────────────────────────────

    @Transactional
    public CouponResponse create(CouponCreateRequest request) {
        if (couponRepository.findByCodeIgnoreCase(request.getCode()).isPresent()) {
            throw new BadRequestException("Coupon code '" + request.getCode() + "' already exists");
        }

        Coupon coupon = Coupon.builder()
                .code(request.getCode().toUpperCase())
                .type(request.getType())
                .value(request.getValue())
                .minOrderAmount(request.getMinOrderAmount() != null
                        ? request.getMinOrderAmount() : BigDecimal.ZERO)
                .maxUses(request.getMaxUses())
                .expiresAt(request.getExpiresAt())
                .build();

        return CouponResponse.from(couponRepository.save(coupon));
    }

    public Page<CouponResponse> getAll(Pageable pageable) {
        return couponRepository.findAll(pageable).map(CouponResponse::from);
    }

    @Transactional
    public CouponResponse deactivate(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id));
        coupon.setIsActive(false);
        return CouponResponse.from(couponRepository.save(coupon));
    }
}
