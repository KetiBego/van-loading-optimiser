package com.ketevan.vanloadingoptimiser.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity(name = "van_load_requests")
public class VanLoadRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "total_volume", nullable = false)
    private BigDecimal totalVolume;

    @Column(name = "total_revenue", nullable = false)
    private BigDecimal totalRevenue;

    @Column(name = "max_volume", nullable = false)
    private BigDecimal maxVolume;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "load_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoadStatus loadStatus;
}
