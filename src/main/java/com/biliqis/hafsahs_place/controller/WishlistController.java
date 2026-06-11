package com.biliqis.hafsahs_place.controller;

import com.biliqis.hafsahs_place.dto.WishlistItemResponse;
import com.biliqis.hafsahs_place.service.WishlistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Wishlist", description = "Save and manage favourite products")
@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<WishlistItemResponse>> getWishlist(Authentication authentication) {
        return ResponseEntity.ok(wishlistService.getWishlist(authentication.getName()));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<WishlistItemResponse> addToWishlist(
            Authentication authentication,
            @PathVariable Long productId) {
        return new ResponseEntity<>(
                wishlistService.addToWishlist(authentication.getName(), productId),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            Authentication authentication,
            @PathVariable Long productId) {
        wishlistService.removeFromWishlist(authentication.getName(), productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}/check")
    public ResponseEntity<Map<String, Boolean>> isInWishlist(
            Authentication authentication,
            @PathVariable Long productId) {
        boolean result = wishlistService.isInWishlist(authentication.getName(), productId);
        return ResponseEntity.ok(Map.of("inWishlist", result));
    }
}
