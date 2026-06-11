package com.biliqis.hafsahs_place.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "custom_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private Category category;

    @Column(name = "design_description", nullable = false, columnDefinition = "TEXT")
    private String designDescription;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reference_images", columnDefinition = "jsonb")
    private List<String> referenceImages;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measurement_id")
    @ToString.Exclude
    private Measurement measurement;

    @Column(name = "preferred_fabric")
    private String preferredFabric;

    @Column(name = "preferred_color", length = 50)
    private String preferredColor;

    @Column(name = "budget_range", length = 100)
    private String budgetRange;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private CustomOrderStatus status = CustomOrderStatus.PENDING;

    @Column(name = "estimated_price", precision = 10, scale = 2)
    private BigDecimal estimatedPrice;

    @Column(name = "final_price", precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "customOrder")
    @Builder.Default
    @ToString.Exclude
    private Set<Payment> payments = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum CustomOrderStatus {
        PENDING,
        QUOTE_SENT,
        APPROVED,
        IN_PRODUCTION,
        READY,
        DELIVERED,
        CANCELLED
    }
}
