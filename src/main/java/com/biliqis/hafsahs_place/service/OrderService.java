package com.biliqis.hafsahs_place.service;

import com.biliqis.hafsahs_place.dto.OrderItemRequest;
import com.biliqis.hafsahs_place.dto.OrderRequest;
import com.biliqis.hafsahs_place.dto.OrderResponse;
import com.biliqis.hafsahs_place.exception.BadRequestException;
import com.biliqis.hafsahs_place.exception.ResourceNotFoundException;
import com.biliqis.hafsahs_place.model.*;
import com.biliqis.hafsahs_place.repository.OrderRepository;
import com.biliqis.hafsahs_place.repository.ProductRepository;
import com.biliqis.hafsahs_place.repository.ProductVariantRepository;
import com.biliqis.hafsahs_place.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CouponService couponService;

    @Transactional
    public OrderResponse placeOrder(String userEmail, OrderRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Order order = Order.builder()
                .user(user)
                .orderNumber(generateOrderNumber())
                .shippingAddress(request.getShippingAddress())
                .shippingCity(request.getShippingCity())
                .shippingState(request.getShippingState())
                .shippingCountry(request.getShippingCountry() != null ? request.getShippingCountry() : "Nigeria")
                .shippingPhone(request.getShippingPhone())
                .notes(request.getNotes())
                .status(Order.OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemReq.getProductId()));

            if (!product.getIsAvailable()) {
                throw new BadRequestException("Product '" + product.getName() + "' is not available");
            }

            // Resolve variant and handle stock
            ProductVariant variant = null;
            if (itemReq.getProductVariantId() != null) {
                variant = variantRepository.findByIdAndProductId(itemReq.getProductVariantId(), product.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "id", itemReq.getProductVariantId()));
                if (!variant.getIsAvailable()) {
                    throw new BadRequestException("Selected variant of '" + product.getName() + "' is not available");
                }
                if (variant.getStockQuantity() < itemReq.getQuantity()) {
                    throw new BadRequestException(
                            "Insufficient stock for variant of '" + product.getName() + "'. Available: " + variant.getStockQuantity());
                }
                variant.setStockQuantity(variant.getStockQuantity() - itemReq.getQuantity());
                variantRepository.save(variant);
            } else if (product.getStockQuantity() != null) {
                if (product.getStockQuantity() < itemReq.getQuantity()) {
                    throw new BadRequestException(
                            "Insufficient stock for '" + product.getName() + "'. Available: " + product.getStockQuantity());
                }
                product.setStockQuantity(product.getStockQuantity() - itemReq.getQuantity());
                productRepository.save(product);
            }

            // Unit price = base price + variant additional price (if any)
            BigDecimal unitPrice = product.getBasePrice();
            if (variant != null) {
                unitPrice = unitPrice.add(variant.getAdditionalPrice());
            }
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productVariant(variant)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(itemTotal)
                    .customMeasurements(itemReq.getCustomMeasurements())
                    .specialInstructions(itemReq.getSpecialInstructions())
                    .build();

            order.getOrderItems().add(orderItem);
            total = total.add(itemTotal);
        }

        // Apply coupon discount if provided
        com.biliqis.hafsahs_place.model.Coupon appliedCoupon = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            appliedCoupon = couponService.validateCoupon(request.getCouponCode(), user, total);
            BigDecimal discount = couponService.calculateDiscount(appliedCoupon, total);
            order.setCouponCode(appliedCoupon.getCode());
            order.setDiscountAmount(discount);
            total = total.subtract(discount);
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);

        // Record coupon usage after the order is persisted
        if (appliedCoupon != null) {
            couponService.recordUsage(appliedCoupon, user, saved);
        }

        return OrderResponse.fromOrder(saved);
    }

    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
        return OrderResponse.fromOrder(order);
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return OrderResponse.fromOrder(order);
    }

    public Page<OrderResponse> getOrdersByUser(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        return orderRepository.findByUserId(user.getId(), pageable)
                .map(OrderResponse::fromOrder);
    }

    public Page<OrderResponse> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable)
                .map(OrderResponse::fromOrder);
    }

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(OrderResponse::fromOrder);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, Order.OrderStatus status, String trackingNumber) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        order.setStatus(status);

        if (status == Order.OrderStatus.SHIPPED && trackingNumber != null && !trackingNumber.isBlank()) {
            order.setTrackingNumber(trackingNumber);
        }

        Order saved = orderRepository.save(order);

        if (status == Order.OrderStatus.CONFIRMED) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() { emailService.sendOrderConfirmation(saved); }
            });
        } else if (status == Order.OrderStatus.SHIPPED) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() { emailService.sendOrderShipped(saved); }
            });
        }

        return OrderResponse.fromOrder(saved);
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("You can only cancel your own orders");
        }

        if (order.getStatus() != Order.OrderStatus.PENDING && order.getStatus() != Order.OrderStatus.CONFIRMED) {
            throw new BadRequestException("Order can only be cancelled when PENDING or CONFIRMED");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);

        // Restore stock for each cancelled item (variant-aware)
        for (OrderItem item : saved.getOrderItems()) {
            if (item.getProductVariant() != null) {
                ProductVariant v = item.getProductVariant();
                v.setStockQuantity(v.getStockQuantity() + item.getQuantity());
                variantRepository.save(v);
            } else {
                Product p = item.getProduct();
                if (p.getStockQuantity() != null) {
                    p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
                    productRepository.save(p);
                }
            }
        }

        return OrderResponse.fromOrder(saved);
    }

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHANUMERIC = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private String generateOrderNumber() {
        String datePart = LocalDate.now().format(DATE_FMT);
        StringBuilder suffix = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            suffix.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return "HP-" + datePart + "-" + suffix;
    }
}
