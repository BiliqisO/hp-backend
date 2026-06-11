package com.biliqis.hafsahs_place.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;

    @NotNull(message = "Consultation type is required")
    private String consultationType; // ONLINE or IN_PERSON

    private String dressType;

    private LocalDate preferredDate;

    private String preferredTime;

    private LocalDate eventDate;

    private String additionalInfo;

    private Boolean interestedInSketches;
}
