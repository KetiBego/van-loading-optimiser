package com.ketevan.vanloadingoptimiser.service;

import com.ketevan.vanloadingoptimiser.algorithm.KnapsackAlgorithm;
import com.ketevan.vanloadingoptimiser.dto.RequestWithShipmentsResponse;
import com.ketevan.vanloadingoptimiser.dto.VanLoadRequestDTO;
import com.ketevan.vanloadingoptimiser.dto.VanLoadResponse;
import com.ketevan.vanloadingoptimiser.entity.LoadStatus;
import com.ketevan.vanloadingoptimiser.entity.VanLoadRequest;
import com.ketevan.vanloadingoptimiser.repository.VanLoadRequestsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VanLoadRequestsService {

    private final ShipmentsService shipmentsService;
    private final VanLoadRequestsRepository vanLoadRequestsRepository;

    private final KnapsackAlgorithm knapsackAlgorithm;

    public VanLoadResponse load(VanLoadRequestDTO vanLoadRequestDTO) {
        VanLoadRequest vanLoadRequest = new VanLoadRequest();
        vanLoadRequest.setMaxVolume(vanLoadRequestDTO.getMaxVolume());
        vanLoadRequest.setTotalVolume(BigDecimal.ZERO);
        vanLoadRequest.setTotalRevenue(BigDecimal.ZERO);
        vanLoadRequest.setCreatedAt(Instant.now());
        vanLoadRequest.setLoadStatus(LoadStatus.CREATED);
        VanLoadRequest request = vanLoadRequestsRepository.save(vanLoadRequest);
        vanLoadRequestDTO.getAvailableShipments().forEach(shipment -> {
            shipmentsService.saveShipment(shipment, request.getId());
        });
        knapsackAlgorithm.calculateShipments(request, shipmentsService.getShipments(request.getId()));
        return new VanLoadResponse(request.getId(), request.getLoadStatus());
    }

    public RequestWithShipmentsResponse getRequest(UUID requestId) {
        VanLoadRequest vanLoadRequest =
                vanLoadRequestsRepository.findById(requestId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "request not found"));
        return new RequestWithShipmentsResponse(
                vanLoadRequest.getId(),
                shipmentsService.getSelectedShipments(vanLoadRequest.getId()),
                vanLoadRequest.getTotalVolume(),
                vanLoadRequest.getTotalRevenue(),
                vanLoadRequest.getCreatedAt(),
                vanLoadRequest.getLoadStatus()
        );
    }

    public List<RequestWithShipmentsResponse> filterRequests(
            LoadStatus loadStatus,
            BigDecimal minRevenue,
            BigDecimal maxRevenue,
            Instant from,
            Instant to
    ) {
        return vanLoadRequestsRepository
                .findFilteredRequests(loadStatus, minRevenue, maxRevenue, from, to)
                .stream().map(vanLoadRequest -> new RequestWithShipmentsResponse(
                        vanLoadRequest.getId(),
                        shipmentsService.getSelectedShipments(vanLoadRequest.getId()),
                        vanLoadRequest.getTotalVolume(),
                        vanLoadRequest.getTotalRevenue(),
                        vanLoadRequest.getCreatedAt(),
                        vanLoadRequest.getLoadStatus()
                )).toList();
    }
}
