package com.ketevan.vanloadingoptimiser.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShipmentDTO {
    @NotNull
    private String name;
    @NotNull
    @DecimalMin(value = "0", inclusive = false, message = "volume of the shipment must be positive")
    private BigDecimal volume;
    @NotNull
    @DecimalMin(value = "0", message = "revenue of the shipment can not be negative")
    private BigDecimal revenue;
}
