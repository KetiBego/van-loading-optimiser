package com.ketevan.vanloadingoptimiser.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VanLoadRequestDTO {
    @NotNull
    @DecimalMin(value = "0", message = "maximum volume can not be negative")
    private BigDecimal maxVolume;
    @NotNull
    @Valid
    private List<ShipmentDTO> availableShipments;
}
