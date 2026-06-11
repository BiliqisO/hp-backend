package com.biliqis.hafsahs_place.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomOrderRequest {

    private Long categoryId;

    @NotBlank(message = "Design description is required")
    private String designDescription;

    private List<String> referenceImages;

    private Long measurementId;

    private String preferredFabric;

    private String preferredColor;

    private String budgetRange;

    private LocalDate eventDate;

    private String notes;
}
