package com.biliqis.hafsahs_place.repository;

import com.biliqis.hafsahs_place.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findByProductIdOrderByCreatedAtAsc(Long productId);

    List<ProductVariant> findByProductIdAndIsAvailableTrueOrderByCreatedAtAsc(Long productId);

    Optional<ProductVariant> findByIdAndProductId(Long id, Long productId);

    boolean existsBySkuAndIdNot(String sku, Long id);

    boolean existsBySku(String sku);
}
