package com.biliqis.hafsahs_place.service;

import com.biliqis.hafsahs_place.dto.CartItemRequest;
import com.biliqis.hafsahs_place.dto.CartItemResponse;
import com.biliqis.hafsahs_place.exception.BadRequestException;
import com.biliqis.hafsahs_place.exception.ResourceNotFoundException;
import com.biliqis.hafsahs_place.model.CartItem;
import com.biliqis.hafsahs_place.model.Product;
import com.biliqis.hafsahs_place.model.ProductVariant;
import com.biliqis.hafsahs_place.model.User;
import com.biliqis.hafsahs_place.repository.CartItemRepository;
import com.biliqis.hafsahs_place.repository.ProductRepository;
import com.biliqis.hafsahs_place.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart(String userEmail) {
        User user = getUser(userEmail);
        return cartItemRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(CartItemResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(String userEmail) {
        return getCart(userEmail).stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public CartItemResponse addItem(String userEmail, CartItemRequest request) {
        User user = getUser(userEmail);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        if (!Boolean.TRUE.equals(product.getIsAvailable())) {
            throw new BadRequestException("Product '" + product.getName() + "' is not available");
        }

        ProductVariant variant = null;
        if (request.getProductVariantId() != null) {
            variant = product.getVariants().stream()
                    .filter(v -> v.getId().equals(request.getProductVariantId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "id", request.getProductVariantId()));
        }

        // Merge into existing cart item if one already exists for same product + variant
        Optional<CartItem> existing = request.getProductVariantId() == null
                ? cartItemRepository.findByUserIdAndProductIdAndProductVariantIdIsNull(user.getId(), product.getId())
                : cartItemRepository.findByUserIdAndProductIdAndProductVariantId(user.getId(), product.getId(), request.getProductVariantId());

        CartItem cartItem;
        if (existing.isPresent()) {
            cartItem = existing.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            cartItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .productVariant(variant)
                    .quantity(request.getQuantity())
                    .build();
        }

        return CartItemResponse.from(cartItemRepository.save(cartItem));
    }

    @Transactional
    public CartItemResponse updateQuantity(String userEmail, Long cartItemId, int quantity) {
        if (quantity < 1) throw new BadRequestException("Quantity must be at least 1");

        CartItem item = getOwnedCartItem(userEmail, cartItemId);
        item.setQuantity(quantity);
        return CartItemResponse.from(cartItemRepository.save(item));
    }

    @Transactional
    public void removeItem(String userEmail, Long cartItemId) {
        CartItem item = getOwnedCartItem(userEmail, cartItemId);
        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(String userEmail) {
        User user = getUser(userEmail);
        cartItemRepository.deleteByUserId(user.getId());
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private CartItem getOwnedCartItem(String userEmail, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));
        if (!item.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("Cart item does not belong to you");
        }
        return item;
    }
}
