package com.biliqis.hafsahs_place.controller;

import com.biliqis.hafsahs_place.dto.CartItemRequest;
import com.biliqis.hafsahs_place.dto.CartItemResponse;
import com.biliqis.hafsahs_place.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Tag(name = "Cart", description = "Shopping cart management")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(Authentication authentication) {
        List<CartItemResponse> items = cartService.getCart(authentication.getName());
        BigDecimal total = items.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(Map.of("items", items, "total", total, "itemCount", items.size()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartItemResponse> addItem(
            Authentication authentication,
            @Valid @RequestBody CartItemRequest request) {
        return new ResponseEntity<>(
                cartService.addItem(authentication.getName(), request),
                HttpStatus.CREATED);
    }

    @PatchMapping("/items/{id}")
    public ResponseEntity<CartItemResponse> updateQuantity(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateQuantity(authentication.getName(), id, quantity));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> removeItem(
            Authentication authentication,
            @PathVariable Long id) {
        cartService.removeItem(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        cartService.clearCart(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
