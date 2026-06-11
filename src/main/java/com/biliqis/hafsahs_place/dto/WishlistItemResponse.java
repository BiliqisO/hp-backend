package com.biliqis.hafsahs_place.dto;

import com.biliqis.hafsahs_place.model.WishlistItem;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WishlistItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String productSlug;
    private String primaryImageUrl;
    private BigDecimal basePrice;
    private Boolean isAvailable;
    private LocalDateTime addedAt;

    public static WishlistItemResponse from(WishlistItem item) {
        String primaryImageUrl = item.getProduct().getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .findFirst()
                .map(img -> img.getImageUrl())
                .orElse(item.getProduct().getImages().stream()
                        .findFirst()
                        .map(img -> img.getImageUrl())
                        .orElse(null));

        return WishlistItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productSlug(item.getProduct().getSlug())
                .primaryImageUrl(primaryImageUrl)
                .basePrice(item.getProduct().getBasePrice())
                .isAvailable(item.getProduct().getIsAvailable())
                .addedAt(item.getCreatedAt())
                .build();
    }
}
