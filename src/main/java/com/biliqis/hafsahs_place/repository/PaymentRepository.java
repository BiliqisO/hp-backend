package com.biliqis.hafsahs_place.repository;

import com.biliqis.hafsahs_place.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentReference(String paymentReference);

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByCustomOrderId(Long customOrderId);
}
