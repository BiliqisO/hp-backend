package com.biliqis.hafsahs_place.service;

import com.biliqis.hafsahs_place.dto.ProductVariantRequest;
import com.biliqis.hafsahs_place.dto.ProductVariantResponse;
import com.biliqis.hafsahs_place.exception.BadRequestException;
import com.biliqis.hafsahs_place.exception.ResourceNotFoundException;
import com.biliqis.hafsahs_place.model.Product;
import com.biliqis.hafsahs_place.model.ProductVariant;
import com.biliqis.hafsahs_place.repository.ProductRepository;
import com.biliqis.hafsahs_place.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductVariantService {

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<ProductVariantResponse> getVariantsByProduct(Long productId, boolean availableOnly) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        List<ProductVariant> variants = availableOnly
                ? variantRepository.findByProductIdAndIsAvailableTrueOrderByCreatedAtAsc(productId)
                : variantRepository.findByProductIdOrderByCreatedAtAsc(productId);
        return variants.stream().map(ProductVariantResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public ProductVariantResponse createVariant(Long productId, ProductVariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (request.getSku() != null && !request.getSku().isBlank()) {
            if (variantRepository.existsBySku(request.getSku())) {
                throw new BadRequestException("SKU '" + request.getSku() + "' is already in use");
            }
        }

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .size(request.getSize())
                .color(request.getColor())
                .additionalPrice(request.getAdditionalPrice() != null ? request.getAdditionalPrice() : BigDecimal.ZERO)
                .stockQuantity(request.getStockQuantity())
                .sku(request.getSku())
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .build();

        return ProductVariantResponse.from(variantRepository.save(variant));
    }

    @Transactional
    public ProductVariantResponse updateVariant(Long productId, Long variantId, ProductVariantRequest request) {
        ProductVariant variant = variantRepository.findByIdAndProductId(variantId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "id", variantId));

        if (request.getSku() != null && !request.getSku().isBlank()) {
            if (variantRepository.existsBySkuAndIdNot(request.getSku(), variantId)) {
                throw new BadRequestException("SKU '" + request.getSku() + "' is already in use");
            }
            variant.setSku(request.getSku());
        }

        if (request.getSize() != null) variant.setSize(request.getSize());
        if (request.getColor() != null) variant.setColor(request.getColor());
        if (request.getAdditionalPrice() != null) variant.setAdditionalPrice(request.getAdditionalPrice());
        if (request.getStockQuantity() != null) variant.setStockQuantity(request.getStockQuantity());
        if (request.getIsAvailable() != null) variant.setIsAvailable(request.getIsAvailable());

        return ProductVariantResponse.from(variantRepository.save(variant));
    }

    @Transactional
    public void deleteVariant(Long productId, Long variantId) {
        ProductVariant variant = variantRepository.findByIdAndProductId(variantId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "id", variantId));
        variantRepository.delete(variant);
    }
}
