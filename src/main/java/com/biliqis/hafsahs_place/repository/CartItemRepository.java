package com.biliqis.hafsahs_place.repository;

import com.biliqis.hafsahs_place.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<CartItem> findByUserIdAndProductIdAndProductVariantIdIsNull(Long userId, Long productId);

    Optional<CartItem> findByUserIdAndProductIdAndProductVariantId(Long userId, Long productId, Long variantId);

    void deleteByUserId(Long userId);
}
