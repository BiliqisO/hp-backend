package com.biliqis.hafsahs_place.dto;

import com.biliqis.hafsahs_place.model.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantResponse {

    private Long id;
    private Long productId;
    private String size;
    private String color;
    private BigDecimal additionalPrice;
    private Integer stockQuantity;
    private String sku;
    private Boolean isAvailable;

    public static ProductVariantResponse from(ProductVariant v) {
        return ProductVariantResponse.builder()
                .id(v.getId())
                .productId(v.getProduct().getId())
                .size(v.getSize())
                .color(v.getColor())
                .additionalPrice(v.getAdditionalPrice())
                .stockQuantity(v.getStockQuantity())
                .sku(v.getSku())
                .isAvailable(v.getIsAvailable())
                .build();
    }
}
