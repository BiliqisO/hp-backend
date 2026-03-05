package com.biliqis.hafsahs_place.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "measurements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(precision = 5, scale = 2)
    private BigDecimal bust;

    @Column(precision = 5, scale = 2)
    private BigDecimal waist;

    @Column(precision = 5, scale = 2)
    private BigDecimal hips;

    @Column(name = "shoulder_width", precision = 5, scale = 2)
    private BigDecimal shoulderWidth;

    @Column(name = "arm_length", precision = 5, scale = 2)
    private BigDecimal armLength;

    @Column(name = "dress_length", precision = 5, scale = 2)
    private BigDecimal dressLength;

    @Column(name = "neck_circumference", precision = 5, scale = 2)
    private BigDecimal neckCircumference;

    @Column(precision = 5, scale = 2)
    private BigDecimal inseam;

    @Column(length = 10)
    @Builder.Default
    private String unit = "cm";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
