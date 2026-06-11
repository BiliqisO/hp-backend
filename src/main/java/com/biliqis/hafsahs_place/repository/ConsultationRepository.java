package com.biliqis.hafsahs_place.repository;

import com.biliqis.hafsahs_place.model.Consultation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    Optional<Consultation> findByBookingNumber(String bookingNumber);

    Page<Consultation> findByEmailOrderByCreatedAtDesc(String email, Pageable pageable);

    Page<Consultation> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Consultation> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Consultation> findByStatusOrderByCreatedAtDesc(Consultation.ConsultationStatus status, Pageable pageable);
}
