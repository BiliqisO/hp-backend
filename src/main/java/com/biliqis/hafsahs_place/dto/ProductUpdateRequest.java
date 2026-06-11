package com.biliqis.hafsahs_place.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateRequest {

    private String name;

    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal basePrice;

    private Long categoryId;

    private Boolean isCustomizable;

    private Boolean isFeatured;

    private Boolean isAvailable;

    private String slug;

    private String sku;

    private String fabricType;

    private String careInstructions;
}
