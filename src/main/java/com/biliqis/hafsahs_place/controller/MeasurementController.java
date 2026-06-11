package com.biliqis.hafsahs_place.controller;

import com.biliqis.hafsahs_place.dto.MeasurementRequest;
import com.biliqis.hafsahs_place.dto.MeasurementResponse;
import com.biliqis.hafsahs_place.service.MeasurementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

    @Autowired
    private MeasurementService measurementService;

    @PostMapping
    public ResponseEntity<MeasurementResponse> saveMeasurement(
            Authentication authentication,
            @Valid @RequestBody MeasurementRequest request) {
        MeasurementResponse response = measurementService.saveMeasurement(authentication.getName(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MeasurementResponse>> getMyMeasurements(Authentication authentication) {
        return ResponseEntity.ok(measurementService.getUserMeasurements(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeasurementResponse> getMeasurement(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(measurementService.getMeasurementById(id, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeasurementResponse> updateMeasurement(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody MeasurementRequest request) {
        return ResponseEntity.ok(measurementService.updateMeasurement(id, authentication.getName(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeasurement(
            @PathVariable Long id,
            Authentication authentication) {
        measurementService.deleteMeasurement(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
