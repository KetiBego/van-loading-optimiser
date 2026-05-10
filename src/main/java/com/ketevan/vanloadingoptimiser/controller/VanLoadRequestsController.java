package com.ketevan.vanloadingoptimiser.controller;

import com.ketevan.vanloadingoptimiser.dto.RequestWithShipmentsResponse;
import com.ketevan.vanloadingoptimiser.dto.VanLoadRequestDTO;
import com.ketevan.vanloadingoptimiser.dto.VanLoadResponse;
import com.ketevan.vanloadingoptimiser.entity.LoadStatus;
import com.ketevan.vanloadingoptimiser.service.VanLoadRequestsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/van")
@RequiredArgsConstructor
@Validated
public class VanLoadRequestsController {
    private final VanLoadRequestsService vanLoadRequestsService;

    @PostMapping("/load")
    public ResponseEntity<VanLoadResponse> load(@RequestBody @Valid VanLoadRequestDTO vanLoadRequestDTO) {
        VanLoadResponse response = vanLoadRequestsService.load(vanLoadRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/request/{id}")
    public ResponseEntity<RequestWithShipmentsResponse> getRequest(@PathVariable UUID id) {
        RequestWithShipmentsResponse response = vanLoadRequestsService.getRequest(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<RequestWithShipmentsResponse>> filterRequests(
            @RequestParam(required = false) LoadStatus status,
            @RequestParam(required = false) @Min(0) BigDecimal minRevenue,
            @RequestParam(required = false) @Min(0) BigDecimal maxRevenue,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to
    ) {
        List<RequestWithShipmentsResponse> requestWithShipmentsResponses =
                vanLoadRequestsService.filterRequests(status, minRevenue, maxRevenue, from, to);
        return ResponseEntity.ok(requestWithShipmentsResponses);
    }
}
