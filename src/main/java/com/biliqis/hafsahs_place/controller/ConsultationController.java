package com.biliqis.hafsahs_place.controller;

import com.biliqis.hafsahs_place.dto.ConsultationRequest;
import com.biliqis.hafsahs_place.dto.ConsultationResponse;
import com.biliqis.hafsahs_place.service.ConsultationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @PostMapping
    public ResponseEntity<ConsultationResponse> bookConsultation(
            @Valid @RequestBody ConsultationRequest request) {
        return new ResponseEntity<>(consultationService.bookConsultation(request), HttpStatus.CREATED);
    }

    @GetMapping("/track/{bookingNumber}")
    public ResponseEntity<ConsultationResponse> trackConsultation(
            @PathVariable String bookingNumber) {
        return ResponseEntity.ok(consultationService.getByBookingNumber(bookingNumber));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ConsultationResponse>> getAllConsultations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(consultationService.getAllConsultations(PageRequest.of(page, size)));
    }

    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ConsultationResponse>> getByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(consultationService.getByStatus(status, PageRequest.of(page, size)));
    }

    @PatchMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConsultationResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(consultationService.updateStatus(id, status));
    }

    @PatchMapping("/admin/{id}/notes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConsultationResponse> addNotes(
            @PathVariable Long id,
            @RequestParam String notes) {
        return ResponseEntity.ok(consultationService.addAdminNotes(id, notes));
    }
}
