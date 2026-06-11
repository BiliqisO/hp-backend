package com.biliqis.hafsahs_place.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal basePrice;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private Boolean isCustomizable = false;

    private Boolean isFeatured = false;

    private Boolean isAvailable = true;

    private String slug;

    private String sku;

    private String fabricType;

    private String careInstructions;
}
