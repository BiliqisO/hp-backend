package com.biliqis.hafsahs_place.controller;

import com.biliqis.hafsahs_place.dto.CouponResponse;
import com.biliqis.hafsahs_place.model.Coupon;
import com.biliqis.hafsahs_place.model.User;
import com.biliqis.hafsahs_place.repository.UserRepository;
import com.biliqis.hafsahs_place.service.CouponService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@Tag(name = "Coupons", description = "Validate and preview discount coupons")
@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Previews a coupon — validates it and returns the discount amount for a given order total.
     * Useful for the checkout UI to show the discount before the order is placed.
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateCoupon(
            Authentication authentication,
            @RequestParam String code,
            @RequestParam BigDecimal orderTotal) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow();
        Coupon coupon = couponService.validateCoupon(code, user, orderTotal);
        BigDecimal discount = couponService.calculateDiscount(coupon, orderTotal);
        return ResponseEntity.ok(Map.of(
                "coupon", CouponResponse.from(coupon),
                "discountAmount", discount,
                "finalTotal", orderTotal.subtract(discount)
        ));
    }
}
