package com.biliqis.hafsahs_place.dto;

import com.biliqis.hafsahs_place.model.Consultation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationResponse {

    private Long id;
    private String bookingNumber;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String consultationType;
    private String dressType;
    private LocalDate preferredDate;
    private String preferredTime;
    private LocalDate eventDate;
    private String additionalInfo;
    private Boolean interestedInSketches;
    private String status;
    private String adminNotes;
    private LocalDateTime createdAt;

    public static ConsultationResponse from(Consultation c) {
        return ConsultationResponse.builder()
                .id(c.getId())
                .bookingNumber(c.getBookingNumber())
                .fullName(c.getFullName())
                .email(c.getEmail())
                .phoneNumber(c.getPhoneNumber())
                .consultationType(c.getConsultationType().name())
                .dressType(c.getDressType())
                .preferredDate(c.getPreferredDate())
                .preferredTime(c.getPreferredTime())
                .eventDate(c.getEventDate())
                .additionalInfo(c.getAdditionalInfo())
                .interestedInSketches(c.getInterestedInSketches())
                .status(c.getStatus().name())
                .adminNotes(c.getAdminNotes())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
