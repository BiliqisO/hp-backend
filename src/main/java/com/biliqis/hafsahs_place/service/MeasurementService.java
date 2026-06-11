package com.biliqis.hafsahs_place.service;

import com.biliqis.hafsahs_place.dto.MeasurementRequest;
import com.biliqis.hafsahs_place.dto.MeasurementResponse;
import com.biliqis.hafsahs_place.exception.BadRequestException;
import com.biliqis.hafsahs_place.exception.ResourceNotFoundException;
import com.biliqis.hafsahs_place.model.Measurement;
import com.biliqis.hafsahs_place.model.User;
import com.biliqis.hafsahs_place.repository.MeasurementRepository;
import com.biliqis.hafsahs_place.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MeasurementService {

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public MeasurementResponse saveMeasurement(String userEmail, MeasurementRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Measurement measurement = Measurement.builder()
                .user(user)
                .bust(request.getBust())
                .waist(request.getWaist())
                .hips(request.getHips())
                .shoulderWidth(request.getShoulderWidth())
                .armLength(request.getArmLength())
                .dressLength(request.getDressLength())
                .neckCircumference(request.getNeckCircumference())
                .inseam(request.getInseam())
                .unit(request.getUnit() != null ? request.getUnit() : "cm")
                .notes(request.getNotes())
                .build();

        return MeasurementResponse.fromMeasurement(measurementRepository.save(measurement));
    }

    public List<MeasurementResponse> getUserMeasurements(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        return measurementRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(MeasurementResponse::fromMeasurement)
                .toList();
    }

    public MeasurementResponse getMeasurementById(Long id, String userEmail) {
        Measurement measurement = measurementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Measurement", "id", id));

        if (!measurement.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("You can only view your own measurements");
        }

        return MeasurementResponse.fromMeasurement(measurement);
    }

    @Transactional
    public MeasurementResponse updateMeasurement(Long id, String userEmail, MeasurementRequest request) {
        Measurement measurement = measurementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Measurement", "id", id));

        if (!measurement.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("You can only update your own measurements");
        }

        if (request.getBust() != null) measurement.setBust(request.getBust());
        if (request.getWaist() != null) measurement.setWaist(request.getWaist());
        if (request.getHips() != null) measurement.setHips(request.getHips());
        if (request.getShoulderWidth() != null) measurement.setShoulderWidth(request.getShoulderWidth());
        if (request.getArmLength() != null) measurement.setArmLength(request.getArmLength());
        if (request.getDressLength() != null) measurement.setDressLength(request.getDressLength());
        if (request.getNeckCircumference() != null) measurement.setNeckCircumference(request.getNeckCircumference());
        if (request.getInseam() != null) measurement.setInseam(request.getInseam());
        if (request.getUnit() != null) measurement.setUnit(request.getUnit());
        if (request.getNotes() != null) measurement.setNotes(request.getNotes());

        return MeasurementResponse.fromMeasurement(measurementRepository.save(measurement));
    }

    @Transactional
    public void deleteMeasurement(Long id, String userEmail) {
        Measurement measurement = measurementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Measurement", "id", id));

        if (!measurement.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("You can only delete your own measurements");
        }

        measurementRepository.delete(measurement);
    }
}
