package com.biliqis.hafsahs_place.dto;

import com.biliqis.hafsahs_place.model.Measurement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeasurementResponse {

    private Long id;
    private BigDecimal bust;
    private BigDecimal waist;
    private BigDecimal hips;
    private BigDecimal shoulderWidth;
    private BigDecimal armLength;
    private BigDecimal dressLength;
    private BigDecimal neckCircumference;
    private BigDecimal inseam;
    private String unit;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MeasurementResponse fromMeasurement(Measurement m) {
        return MeasurementResponse.builder()
                .id(m.getId())
                .bust(m.getBust())
                .waist(m.getWaist())
                .hips(m.getHips())
                .shoulderWidth(m.getShoulderWidth())
                .armLength(m.getArmLength())
                .dressLength(m.getDressLength())
                .neckCircumference(m.getNeckCircumference())
                .inseam(m.getInseam())
                .unit(m.getUnit())
                .notes(m.getNotes())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }
}
