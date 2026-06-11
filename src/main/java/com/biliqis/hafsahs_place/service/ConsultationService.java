package com.biliqis.hafsahs_place.service;

import com.biliqis.hafsahs_place.dto.ConsultationRequest;
import com.biliqis.hafsahs_place.dto.ConsultationResponse;
import com.biliqis.hafsahs_place.exception.ResourceNotFoundException;
import com.biliqis.hafsahs_place.model.Consultation;
import com.biliqis.hafsahs_place.model.User;
import com.biliqis.hafsahs_place.repository.ConsultationRepository;
import com.biliqis.hafsahs_place.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private UserRepository userRepository;

    public ConsultationResponse bookConsultation(ConsultationRequest request) {
        Consultation consultation = Consultation.builder()
                .bookingNumber("CON-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .consultationType(Consultation.ConsultationType.valueOf(request.getConsultationType()))
                .dressType(request.getDressType())
                .preferredDate(request.getPreferredDate())
                .preferredTime(request.getPreferredTime())
                .eventDate(request.getEventDate())
                .additionalInfo(request.getAdditionalInfo())
                .interestedInSketches(request.getInterestedInSketches() != null && request.getInterestedInSketches())
                .build();

        // Attach user if authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            userRepository.findByEmail(auth.getName()).ifPresent(consultation::setUser);
        }

        return ConsultationResponse.from(consultationRepository.save(consultation));
    }

    public ConsultationResponse getByBookingNumber(String bookingNumber) {
        Consultation c = consultationRepository.findByBookingNumber(bookingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found"));
        return ConsultationResponse.from(c);
    }

    // Admin methods
    public Page<ConsultationResponse> getAllConsultations(Pageable pageable) {
        return consultationRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(ConsultationResponse::from);
    }

    public Page<ConsultationResponse> getByStatus(String status, Pageable pageable) {
        Consultation.ConsultationStatus s = Consultation.ConsultationStatus.valueOf(status);
        return consultationRepository.findByStatusOrderByCreatedAtDesc(s, pageable)
                .map(ConsultationResponse::from);
    }

    public ConsultationResponse updateStatus(Long id, String status) {
        Consultation c = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found"));
        c.setStatus(Consultation.ConsultationStatus.valueOf(status));
        return ConsultationResponse.from(consultationRepository.save(c));
    }

    public ConsultationResponse addAdminNotes(Long id, String notes) {
        Consultation c = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found"));
        c.setAdminNotes(notes);
        return ConsultationResponse.from(consultationRepository.save(c));
    }
}
