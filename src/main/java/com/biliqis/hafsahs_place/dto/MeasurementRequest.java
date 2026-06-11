package com.biliqis.hafsahs_place.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementRequest {

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
}
