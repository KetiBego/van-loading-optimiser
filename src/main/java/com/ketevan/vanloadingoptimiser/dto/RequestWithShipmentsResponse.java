package com.ketevan.vanloadingoptimiser.dto;

import com.ketevan.vanloadingoptimiser.entity.LoadStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class RequestWithShipmentsResponse {
    private UUID requestId;
    private List<ShipmentDTO> selectedShipments;
    private BigDecimal totalVolume;
    private BigDecimal totalRevenue;
    private Instant createdAt;
    private LoadStatus loadStatus;
}
