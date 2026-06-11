package com.biliqis.hafsahs_place.dto;

import com.biliqis.hafsahs_place.model.CartItem;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CartItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String productSlug;
    private String primaryImageUrl;
    private BigDecimal unitPrice;
    private Long productVariantId;
    private String variantSize;
    private String variantColor;
    private BigDecimal variantAdditionalPrice;
    private Integer quantity;
    private BigDecimal lineTotal;
    private LocalDateTime addedAt;

    public static CartItemResponse from(CartItem item) {
        BigDecimal unitPrice = item.getProduct().getBasePrice();
        if (item.getProductVariant() != null && item.getProductVariant().getAdditionalPrice() != null) {
            unitPrice = unitPrice.add(item.getProductVariant().getAdditionalPrice());
        }

        String primaryImageUrl = item.getProduct().getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .findFirst()
                .map(img -> img.getImageUrl())
                .orElse(item.getProduct().getImages().stream()
                        .findFirst()
                        .map(img -> img.getImageUrl())
                        .orElse(null));

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productSlug(item.getProduct().getSlug())
                .primaryImageUrl(primaryImageUrl)
                .unitPrice(unitPrice)
                .productVariantId(item.getProductVariant() != null ? item.getProductVariant().getId() : null)
                .variantSize(item.getProductVariant() != null ? item.getProductVariant().getSize() : null)
                .variantColor(item.getProductVariant() != null ? item.getProductVariant().getColor() : null)
                .variantAdditionalPrice(item.getProductVariant() != null ? item.getProductVariant().getAdditionalPrice() : null)
                .quantity(item.getQuantity())
                .lineTotal(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())))
                .addedAt(item.getCreatedAt())
                .build();
    }
}
