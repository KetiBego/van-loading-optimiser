package com.ketevan.vanloadingoptimiser.dto;

import com.ketevan.vanloadingoptimiser.entity.LoadStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class VanLoadResponse {
    private UUID requestId;
    private LoadStatus loadStatus;
}
