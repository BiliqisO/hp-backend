package com.biliqis.hafsahs_place.repository;

import com.biliqis.hafsahs_place.model.CustomOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomOrderRepository extends JpaRepository<CustomOrder, Long> {

    Optional<CustomOrder> findByOrderNumber(String orderNumber);

    Page<CustomOrder> findByUserId(Long userId, Pageable pageable);

    List<CustomOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<CustomOrder> findByStatus(CustomOrder.CustomOrderStatus status, Pageable pageable);
}
