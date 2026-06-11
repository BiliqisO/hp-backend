package com.biliqis.hafsahs_place.service;

import com.biliqis.hafsahs_place.dto.PaymentResponse;
import com.biliqis.hafsahs_place.exception.BadRequestException;
import com.biliqis.hafsahs_place.exception.ResourceNotFoundException;
import com.biliqis.hafsahs_place.model.Order;
import com.biliqis.hafsahs_place.model.Payment;
import com.biliqis.hafsahs_place.repository.OrderRepository;
import com.biliqis.hafsahs_place.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaystackService paystackService;

    @Autowired
    private EmailService emailService;

    @Transactional
    public PaymentResponse initializePayment(Long orderId, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("You can only pay for your own orders");
        }

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new BadRequestException("Payment can only be initialized for PENDING orders");
        }

        // Check if there's already a pending payment
        paymentRepository.findByOrderId(orderId).ifPresent(existing -> {
            if (existing.getStatus() == Payment.PaymentStatus.PENDING) {
                throw new BadRequestException("A payment is already pending for this order");
            }
        });

        String reference = "HP-PAY-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        Payment payment = Payment.builder()
                .order(order)
                .paymentReference(reference)
                .paymentMethod("paystack")
                .amount(order.getTotalAmount())
                .status(Payment.PaymentStatus.PENDING)
                .build();

        payment = paymentRepository.save(payment);

        // Initialize with Paystack
        Map<String, Object> paystackResponse = paystackService.initializePayment(
                userEmail, order.getTotalAmount(), reference);

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) paystackResponse.get("data");
        String authorizationUrl = data != null ? (String) data.get("authorization_url") : null;

        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(orderId)
                .paymentReference(reference)
                .paymentMethod("paystack")
                .amount(order.getTotalAmount())
                .status(payment.getStatus().name())
                .authorizationUrl(authorizationUrl)
                .createdAt(payment.getCreatedAt())
                .build();
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public PaymentResponse verifyPayment(String reference) {
        Payment payment = paymentRepository.findByPaymentReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "reference", reference));

        Map<String, Object> verifyResponse = paystackService.verifyPayment(reference);

        Map<String, Object> data = (Map<String, Object>) verifyResponse.get("data");
        String status = data != null ? (String) data.get("status") : "failed";

        if ("success".equals(status)) {
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            payment.setGatewayResponse(data);

            // Update order status and send confirmation email
            if (payment.getOrder() != null) {
                Order confirmedOrder = payment.getOrder();
                confirmedOrder.setStatus(Order.OrderStatus.CONFIRMED);
                orderRepository.save(confirmedOrder);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        emailService.sendOrderConfirmation(confirmedOrder);
                    }
                });
            }
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setGatewayResponse(data);
        }

        paymentRepository.save(payment);

        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
                .paymentReference(payment.getPaymentReference())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public PaymentResponse getPaymentByReference(String reference) {
        Payment payment = paymentRepository.findByPaymentReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "reference", reference));

        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
                .paymentReference(payment.getPaymentReference())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
