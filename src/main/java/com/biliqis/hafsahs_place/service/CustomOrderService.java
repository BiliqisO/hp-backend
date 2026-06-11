package com.biliqis.hafsahs_place.service;

import com.biliqis.hafsahs_place.dto.CustomOrderRequest;
import com.biliqis.hafsahs_place.dto.CustomOrderResponse;
import com.biliqis.hafsahs_place.exception.BadRequestException;
import com.biliqis.hafsahs_place.exception.ResourceNotFoundException;
import com.biliqis.hafsahs_place.model.Category;
import com.biliqis.hafsahs_place.model.CustomOrder;
import com.biliqis.hafsahs_place.model.Measurement;
import com.biliqis.hafsahs_place.model.User;
import com.biliqis.hafsahs_place.repository.CategoryRepository;
import com.biliqis.hafsahs_place.repository.CustomOrderRepository;
import com.biliqis.hafsahs_place.repository.MeasurementRepository;
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
public class CustomOrderService {

    @Autowired
    private CustomOrderRepository customOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public CustomOrderResponse createCustomOrder(String userEmail, CustomOrderRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        CustomOrder.CustomOrderBuilder builder = CustomOrder.builder()
                .user(user)
                .orderNumber(generateOrderNumber())
                .designDescription(request.getDesignDescription())
                .referenceImages(request.getReferenceImages())
                .preferredFabric(request.getPreferredFabric())
                .preferredColor(request.getPreferredColor())
                .budgetRange(request.getBudgetRange())
                .eventDate(request.getEventDate())
                .notes(request.getNotes())
                .status(CustomOrder.CustomOrderStatus.PENDING);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            builder.category(category);
        }

        if (request.getMeasurementId() != null) {
            Measurement measurement = measurementRepository.findById(request.getMeasurementId())
                    .orElseThrow(() -> new ResourceNotFoundException("Measurement", "id", request.getMeasurementId()));
            builder.measurement(measurement);
        }

        return CustomOrderResponse.fromCustomOrder(customOrderRepository.save(builder.build()));
    }

    public CustomOrderResponse getCustomOrderByNumber(String orderNumber) {
        CustomOrder co = customOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("CustomOrder", "orderNumber", orderNumber));
        return CustomOrderResponse.fromCustomOrder(co);
    }

    public CustomOrderResponse getCustomOrderById(Long id) {
        CustomOrder co = customOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomOrder", "id", id));
        return CustomOrderResponse.fromCustomOrder(co);
    }

    public Page<CustomOrderResponse> getCustomOrdersByUser(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        return customOrderRepository.findByUserId(user.getId(), pageable)
                .map(CustomOrderResponse::fromCustomOrder);
    }

    public Page<CustomOrderResponse> getAllCustomOrders(Pageable pageable) {
        return customOrderRepository.findAll(pageable)
                .map(CustomOrderResponse::fromCustomOrder);
    }

    @Transactional
    public CustomOrderResponse updateStatus(Long id, CustomOrder.CustomOrderStatus status) {
        CustomOrder co = customOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomOrder", "id", id));
        co.setStatus(status);
        CustomOrder saved = customOrderRepository.save(co);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                emailService.sendCustomOrderStatusUpdate(saved);
            }
        });
        return CustomOrderResponse.fromCustomOrder(saved);
    }

    @Transactional
    public CustomOrderResponse setPrice(Long id, BigDecimal estimatedPrice, BigDecimal finalPrice) {
        CustomOrder co = customOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomOrder", "id", id));

        if (estimatedPrice != null) co.setEstimatedPrice(estimatedPrice);
        if (finalPrice != null) co.setFinalPrice(finalPrice);

        return CustomOrderResponse.fromCustomOrder(customOrderRepository.save(co));
    }

    @Transactional
    public CustomOrderResponse cancelCustomOrder(Long id, String userEmail) {
        CustomOrder co = customOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomOrder", "id", id));

        if (!co.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("You can only cancel your own custom orders");
        }

        if (co.getStatus() != CustomOrder.CustomOrderStatus.PENDING
                && co.getStatus() != CustomOrder.CustomOrderStatus.QUOTE_SENT) {
            throw new BadRequestException("Custom order can only be cancelled when PENDING or QUOTE_SENT");
        }

        co.setStatus(CustomOrder.CustomOrderStatus.CANCELLED);
        return CustomOrderResponse.fromCustomOrder(customOrderRepository.save(co));
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
        return "HPC-" + datePart + "-" + suffix;
    }
}
