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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock CartItemRepository cartItemRepository;
    @Mock ProductRepository productRepository;
    @Mock UserRepository userRepository;

    @InjectMocks CartService cartService;

    private User user;
    private Product product;
    private ProductVariant variant;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("user@test.com").build();

        variant = ProductVariant.builder()
                .id(10L).size("M").color("Red")
                .additionalPrice(BigDecimal.valueOf(500))
                .stockQuantity(5).isAvailable(true)
                .build();

        product = Product.builder()
                .id(100L).name("Test Dress")
                .basePrice(BigDecimal.valueOf(15000))
                .isAvailable(true)
                .variants(new HashSet<>(java.util.Set.of(variant)))
                .build();
        variant.setProduct(product);
    }

    private CartItemRequest itemRequest(Long productId, Long variantId, int quantity) {
        CartItemRequest r = new CartItemRequest();
        r.setProductId(productId);
        r.setProductVariantId(variantId);
        r.setQuantity(quantity);
        return r;
    }

    // ── addItem ───────────────────────────────────────────────────────────────

    @Test
    void addItem_newItem_createsCartItem() {
        CartItemRequest request = itemRequest(100L, null, 2);
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductIdAndProductVariantIdIsNull(1L, 100L))
                .thenReturn(Optional.empty());

        CartItem saved = CartItem.builder().id(1L).user(user).product(product).quantity(2).build();
        when(cartItemRepository.save(any())).thenReturn(saved);

        CartItemResponse response = cartService.addItem("user@test.com", request);

        assertThat(response).isNotNull();
        verify(cartItemRepository).save(argThat(item -> item.getQuantity() == 2));
    }

    @Test
    void addItem_existingItem_mergesQuantity() {
        CartItemRequest request = itemRequest(100L, null, 3);
        CartItem existing = CartItem.builder().id(1L).user(user).product(product).quantity(2).build();

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductIdAndProductVariantIdIsNull(1L, 100L))
                .thenReturn(Optional.of(existing));
        when(cartItemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        cartService.addItem("user@test.com", request);

        // quantity should be 2 + 3 = 5
        verify(cartItemRepository).save(argThat(item -> item.getQuantity() == 5));
    }

    @Test
    void addItem_withVariant_mergesVariantItem() {
        CartItemRequest request = itemRequest(100L, 10L, 1);
        CartItem existing = CartItem.builder().id(2L).user(user).product(product)
                .productVariant(variant).quantity(1).build();

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByUserIdAndProductIdAndProductVariantId(1L, 100L, 10L))
                .thenReturn(Optional.of(existing));
        when(cartItemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        cartService.addItem("user@test.com", request);

        verify(cartItemRepository).save(argThat(item -> item.getQuantity() == 2));
    }

    @Test
    void addItem_productUnavailable_throws() {
        product.setIsAvailable(false);
        CartItemRequest request = itemRequest(100L, null, 1);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> cartService.addItem("user@test.com", request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not available");
    }

    @Test
    void addItem_variantNotFound_throws() {
        CartItemRequest request = itemRequest(100L, 999L, 1);

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> cartService.addItem("user@test.com", request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── updateQuantity ────────────────────────────────────────────────────────

    @Test
    void updateQuantity_success_updatesItem() {
        CartItem item = CartItem.builder().id(1L).user(user).product(product).quantity(2).build();
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartItemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        cartService.updateQuantity("user@test.com", 1L, 5);

        verify(cartItemRepository).save(argThat(i -> i.getQuantity() == 5));
    }

    @Test
    void updateQuantity_notOwner_throws() {
        User other = User.builder().id(2L).email("other@test.com").build();
        CartItem item = CartItem.builder().id(1L).user(other).product(product).quantity(2).build();
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> cartService.updateQuantity("user@test.com", 1L, 5))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("does not belong");
    }

    @Test
    void updateQuantity_belowOne_throws() {
        assertThatThrownBy(() -> cartService.updateQuantity("user@test.com", 1L, 0))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("at least 1");
    }

    // ── clearCart ─────────────────────────────────────────────────────────────

    @Test
    void clearCart_deletesAllItemsForUser() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        cartService.clearCart("user@test.com");

        verify(cartItemRepository).deleteByUserId(1L);
    }
}
