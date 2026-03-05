package com.biliqis.hafsahs_place.repository;

import com.biliqis.hafsahs_place.model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    List<Measurement> findByUserIdOrderByCreatedAtDesc(Long userId);
}
