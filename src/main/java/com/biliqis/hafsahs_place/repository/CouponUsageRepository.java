package com.biliqis.hafsahs_place.repository;

import com.biliqis.hafsahs_place.model.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {

    boolean existsByCouponIdAndUserId(Long couponId, Long userId);
}
