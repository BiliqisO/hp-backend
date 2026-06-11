package com.biliqis.hafsahs_place.controller;

import com.biliqis.hafsahs_place.dto.CustomOrderRequest;
import com.biliqis.hafsahs_place.dto.CustomOrderResponse;
import com.biliqis.hafsahs_place.model.CustomOrder;
import com.biliqis.hafsahs_place.service.CustomOrderService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "Custom Orders", description = "Request and manage bespoke design orders")
@RestController
@RequestMapping("/api/custom-orders")
public class CustomOrderController {

    @Autowired
    private CustomOrderService customOrderService;

    @PostMapping
    public ResponseEntity<CustomOrderResponse> createCustomOrder(
            Authentication authentication,
            @Valid @RequestBody CustomOrderRequest request) {
        CustomOrderResponse response = customOrderService.createCustomOrder(authentication.getName(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<CustomOrderResponse>> getMyCustomOrders(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(customOrderService.getCustomOrdersByUser(authentication.getName(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomOrderResponse> getCustomOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(customOrderService.getCustomOrderById(id));
    }

    @GetMapping("/track/{orderNumber}")
    public ResponseEntity<CustomOrderResponse> trackCustomOrder(@PathVariable String orderNumber) {
        return ResponseEntity.ok(customOrderService.getCustomOrderByNumber(orderNumber));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<CustomOrderResponse> cancelCustomOrder(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(customOrderService.cancelCustomOrder(id, authentication.getName()));
    }

    // --- Admin endpoints ---

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CustomOrderResponse>> getAllCustomOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(customOrderService.getAllCustomOrders(pageable));
    }

    @PatchMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomOrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam CustomOrder.CustomOrderStatus status) {
        return ResponseEntity.ok(customOrderService.updateStatus(id, status));
    }

    @PatchMapping("/admin/{id}/price")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomOrderResponse> setPrice(
            @PathVariable Long id,
            @RequestParam(required = false) BigDecimal estimatedPrice,
            @RequestParam(required = false) BigDecimal finalPrice) {
        return ResponseEntity.ok(customOrderService.setPrice(id, estimatedPrice, finalPrice));
    }
}
