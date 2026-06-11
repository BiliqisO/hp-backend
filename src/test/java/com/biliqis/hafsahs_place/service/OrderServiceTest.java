package com.biliqis.hafsahs_place.service;

import com.biliqis.hafsahs_place.dto.OrderRequest;
import com.biliqis.hafsahs_place.dto.OrderResponse;
import com.biliqis.hafsahs_place.dto.OrderItemRequest;
import com.biliqis.hafsahs_place.exception.BadRequestException;
import com.biliqis.hafsahs_place.model.*;
import com.biliqis.hafsahs_place.repository.OrderRepository;
import com.biliqis.hafsahs_place.repository.ProductRepository;
import com.biliqis.hafsahs_place.repository.ProductVariantRepository;
import com.biliqis.hafsahs_place.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock ProductRepository productRepository;
    @Mock ProductVariantRepository variantRepository;
    @Mock UserRepository userRepository;
    @Mock EmailService emailService;
    @Mock CouponService couponService;

    @InjectMocks OrderService orderService;

    private User user;
    private Product product;
    private OrderRequest baseRequest;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("user@test.com").firstName("Amira").build();

        product = Product.builder()
                .id(100L).name("Test Dress")
                .basePrice(BigDecimal.valueOf(20000))
                .isAvailable(true)
                .stockQuantity(10)
                .variants(new HashSet<>())
                .build();

        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId(100L);
        itemReq.setQuantity(2);
        baseRequest = new OrderRequest();
        baseRequest.setItems(List.of(itemReq));
        baseRequest.setShippingAddress("123 Test St");
        baseRequest.setShippingCity("Lagos");
        baseRequest.setShippingState("Lagos");
        baseRequest.setShippingPhone("08012345678");
    }

    // ── placeOrder ────────────────────────────────────────────────────────────

    @Test
    void placeOrder_success_createsOrderAndDecrementsStock() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));

        Order savedOrder = Order.builder()
                .id(1L).orderNumber("HP-20260101-ABCDEF")
                .user(user).totalAmount(BigDecimal.valueOf(40000))
                .orderItems(new HashSet<>())
                .status(Order.OrderStatus.PENDING)
                .build();
        when(orderRepository.save(any())).thenReturn(savedOrder);

        OrderResponse response = orderService.placeOrder("user@test.com", baseRequest);

        assertThat(response).isNotNull();
        assertThat(response.getOrderNumber()).isEqualTo("HP-20260101-ABCDEF");
        // stock decremented by 2
        assertThat(product.getStockQuantity()).isEqualTo(8);
        verify(productRepository).save(product);
    }

    @Test
    void placeOrder_productUnavailable_throws() {
        product.setIsAvailable(false);
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> orderService.placeOrder("user@test.com", baseRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not available");
    }

    @Test
    void placeOrder_insufficientProductStock_throws() {
        product.setStockQuantity(1); // only 1 in stock, requesting 2
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> orderService.placeOrder("user@test.com", baseRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void placeOrder_variantInsufficientStock_throws() {
        ProductVariant variant = ProductVariant.builder()
                .id(10L).stockQuantity(1).isAvailable(true)
                .additionalPrice(BigDecimal.ZERO).build();
        variant.setProduct(product);

        OrderItemRequest itemWithVariant = new OrderItemRequest();
        itemWithVariant.setProductId(100L);
        itemWithVariant.setProductVariantId(10L);
        itemWithVariant.setQuantity(2);
        baseRequest.setItems(List.of(itemWithVariant));

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(variantRepository.findByIdAndProductId(10L, 100L)).thenReturn(Optional.of(variant));

        assertThatThrownBy(() -> orderService.placeOrder("user@test.com", baseRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Insufficient stock for variant");
    }

    @Test
    void placeOrder_withCoupon_appliesDiscount() {
        baseRequest.setCouponCode("SAVE20");

        Coupon coupon = Coupon.builder()
                .id(1L).code("SAVE20")
                .type(Coupon.CouponType.PERCENTAGE).value(BigDecimal.valueOf(20))
                .minOrderAmount(BigDecimal.ZERO)
                .isActive(true).usedCount(0)
                .build();

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(couponService.validateCoupon(eq("SAVE20"), eq(user), any())).thenReturn(coupon);
        when(couponService.calculateDiscount(eq(coupon), any()))
                .thenReturn(BigDecimal.valueOf(8000)); // 20% of 40000

        Order savedOrder = Order.builder()
                .id(1L).orderNumber("HP-20260101-XXXXXX")
                .user(user).totalAmount(BigDecimal.valueOf(32000))
                .discountAmount(BigDecimal.valueOf(8000))
                .couponCode("SAVE20")
                .orderItems(new HashSet<>())
                .status(Order.OrderStatus.PENDING)
                .build();
        when(orderRepository.save(any())).thenReturn(savedOrder);

        OrderResponse response = orderService.placeOrder("user@test.com", baseRequest);

        assertThat(response.getDiscountAmount()).isEqualByComparingTo(BigDecimal.valueOf(8000));
        verify(couponService).recordUsage(eq(coupon), eq(user), any());
    }

    // ── cancelOrder ───────────────────────────────────────────────────────────

    @Test
    void cancelOrder_success_cancelsAndRestoresStock() {
        OrderItem item = OrderItem.builder()
                .product(product).quantity(2).build();
        Order order = Order.builder()
                .id(1L).user(user)
                .status(Order.OrderStatus.PENDING)
                .orderItems(new HashSet<>(Set.of(item)))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        orderService.cancelOrder(1L, "user@test.com");

        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.CANCELLED);
        assertThat(product.getStockQuantity()).isEqualTo(12); // 10 + 2
    }

    @Test
    void cancelOrder_notOwner_throws() {
        User other = User.builder().id(2L).email("other@test.com").build();
        Order order = Order.builder()
                .id(1L).user(other)
                .status(Order.OrderStatus.PENDING)
                .orderItems(new HashSet<>())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L, "user@test.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("your own orders");
    }

    @Test
    void cancelOrder_shippedOrder_throws() {
        Order order = Order.builder()
                .id(1L).user(user)
                .status(Order.OrderStatus.SHIPPED)
                .orderItems(new HashSet<>())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L, "user@test.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("PENDING or CONFIRMED");
    }
}
