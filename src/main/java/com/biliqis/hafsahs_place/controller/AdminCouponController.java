package com.biliqis.hafsahs_place.controller;

import com.biliqis.hafsahs_place.dto.CouponCreateRequest;
import com.biliqis.hafsahs_place.dto.CouponResponse;
import com.biliqis.hafsahs_place.service.CouponService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin — Coupons", description = "Create and manage discount coupons")
@RestController
@RequestMapping("/api/admin/coupons")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping
    public ResponseEntity<Page<CouponResponse>> getAllCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(couponService.getAll(pageable));
    }

    @PostMapping
    public ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CouponCreateRequest request) {
        return new ResponseEntity<>(couponService.create(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CouponResponse> deactivateCoupon(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.deactivate(id));
    }
}
