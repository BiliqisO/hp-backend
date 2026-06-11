package com.biliqis.hafsahs_place.controller;

import com.biliqis.hafsahs_place.dto.ProductVariantRequest;
import com.biliqis.hafsahs_place.dto.ProductVariantResponse;
import com.biliqis.hafsahs_place.service.ProductVariantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product Variants", description = "Manage sizes, colours, and stock per product variant")
@RestController
@RequestMapping("/api/products/{productId}/variants")
public class ProductVariantController {

    @Autowired
    private ProductVariantService variantService;

    @GetMapping
    public ResponseEntity<List<ProductVariantResponse>> getVariants(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "false") boolean availableOnly) {
        return ResponseEntity.ok(variantService.getVariantsByProduct(productId, availableOnly));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductVariantResponse> createVariant(
            @PathVariable Long productId,
            @Valid @RequestBody ProductVariantRequest request) {
        return new ResponseEntity<>(variantService.createVariant(productId, request), HttpStatus.CREATED);
    }

    @PutMapping("/{variantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductVariantResponse> updateVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @Valid @RequestBody ProductVariantRequest request) {
        return ResponseEntity.ok(variantService.updateVariant(productId, variantId, request));
    }

    @DeleteMapping("/{variantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId) {
        variantService.deleteVariant(productId, variantId);
        return ResponseEntity.noContent().build();
    }
}
