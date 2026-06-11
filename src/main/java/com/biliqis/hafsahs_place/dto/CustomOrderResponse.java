package com.biliqis.hafsahs_place.dto;

import com.biliqis.hafsahs_place.model.CustomOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomOrderResponse {

    private Long id;
    private String orderNumber;
    private String status;
    private Long categoryId;
    private String categoryName;
    private String designDescription;
    private List<String> referenceImages;
    private String preferredFabric;
    private String preferredColor;
    private String budgetRange;
    private LocalDate eventDate;
    private BigDecimal estimatedPrice;
    private BigDecimal finalPrice;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CustomOrderResponse fromCustomOrder(CustomOrder co) {
        return CustomOrderResponse.builder()
                .id(co.getId())
                .orderNumber(co.getOrderNumber())
                .status(co.getStatus().name())
                .categoryId(co.getCategory() != null ? co.getCategory().getId() : null)
                .categoryName(co.getCategory() != null ? co.getCategory().getName() : null)
                .designDescription(co.getDesignDescription())
                .referenceImages(co.getReferenceImages())
                .preferredFabric(co.getPreferredFabric())
                .preferredColor(co.getPreferredColor())
                .budgetRange(co.getBudgetRange())
                .eventDate(co.getEventDate())
                .estimatedPrice(co.getEstimatedPrice())
                .finalPrice(co.getFinalPrice())
                .notes(co.getNotes())
                .createdAt(co.getCreatedAt())
                .updatedAt(co.getUpdatedAt())
                .build();
    }
}
