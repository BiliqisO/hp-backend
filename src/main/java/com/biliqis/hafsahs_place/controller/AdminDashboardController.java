package com.biliqis.hafsahs_place.controller;

import com.biliqis.hafsahs_place.dto.DashboardResponse;
import com.biliqis.hafsahs_place.model.CustomOrder;
import com.biliqis.hafsahs_place.model.Order;
import com.biliqis.hafsahs_place.repository.CustomOrderRepository;
import com.biliqis.hafsahs_place.repository.OrderRepository;
import com.biliqis.hafsahs_place.repository.ProductRepository;
import com.biliqis.hafsahs_place.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin", description = "Admin-only endpoints — dashboard stats and order management")
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomOrderRepository customOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        DashboardResponse stats = DashboardResponse.builder()
                .totalOrders(orderRepository.count())
                .pendingOrders(orderRepository.countByStatus(Order.OrderStatus.PENDING))
                .confirmedOrders(orderRepository.countByStatus(Order.OrderStatus.CONFIRMED))
                .totalRevenue(orderRepository.sumRevenueExcluding(Order.OrderStatus.CANCELLED))
                .totalProducts(productRepository.count())
                .availableProducts(productRepository.countByIsAvailableTrue())
                .totalCustomers(userRepository.count())
                .totalCustomOrders(customOrderRepository.count())
                .pendingCustomOrders(customOrderRepository.countByStatus(CustomOrder.CustomOrderStatus.PENDING))
                .build();
        return ResponseEntity.ok(stats);
    }
}
