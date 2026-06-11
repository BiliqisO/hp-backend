package com.biliqis.hafsahs_place.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardResponse {

    private long totalOrders;
    private long pendingOrders;
    private long confirmedOrders;
    private BigDecimal totalRevenue;

    private long totalProducts;
    private long availableProducts;

    private long totalCustomers;

    private long totalCustomOrders;
    private long pendingCustomOrders;
}
