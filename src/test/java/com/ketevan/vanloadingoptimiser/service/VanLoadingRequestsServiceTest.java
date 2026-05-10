package com.ketevan.vanloadingoptimiser.service;

import com.ketevan.vanloadingoptimiser.algorithm.KnapsackAlgorithm;
import com.ketevan.vanloadingoptimiser.dto.RequestWithShipmentsResponse;
import com.ketevan.vanloadingoptimiser.dto.ShipmentDTO;
import com.ketevan.vanloadingoptimiser.dto.VanLoadRequestDTO;
import com.ketevan.vanloadingoptimiser.entity.LoadStatus;
import com.ketevan.vanloadingoptimiser.entity.Shipment;
import com.ketevan.vanloadingoptimiser.entity.VanLoadRequest;
import com.ketevan.vanloadingoptimiser.repository.VanLoadRequestsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VanLoadingRequestsServiceTest {

    @Mock
    private ShipmentsService shipmentsService;
    @Mock
    private VanLoadRequestsRepository vanLoadRequestsRepository;
    @Mock
    private KnapsackAlgorithm knapsackAlgorithm;

    @InjectMocks
    private VanLoadRequestsService vanLoadRequestsService;

    @Test
    void load_shouldCreateVanLoadRequestAndSaveInRepository() {
        VanLoadRequestDTO vanLoadRequestDTO = new VanLoadRequestDTO();
        vanLoadRequestDTO.setMaxVolume(BigDecimal.valueOf(15));

        ShipmentDTO shipmentDTO = new ShipmentDTO();
        shipmentDTO.setName("Shipment");
        shipmentDTO.setVolume(BigDecimal.valueOf(5));
        shipmentDTO.setRevenue(BigDecimal.valueOf(120));

        List<ShipmentDTO> shipmentDTOS = new ArrayList<>();
        shipmentDTOS.add(shipmentDTO);
        vanLoadRequestDTO.setAvailableShipments(shipmentDTOS);

        VanLoadRequest vanLoadRequest = new VanLoadRequest();
        UUID requestId = UUID.randomUUID();
        vanLoadRequest.setId(requestId);
        vanLoadRequest.setMaxVolume(vanLoadRequestDTO.getMaxVolume());
        vanLoadRequest.setTotalVolume(BigDecimal.valueOf(0));
        vanLoadRequest.setTotalRevenue(BigDecimal.valueOf(0));
        vanLoadRequest.setCreatedAt(Instant.now());

        when(vanLoadRequestsRepository.save(any(VanLoadRequest.class))).thenReturn(vanLoadRequest);

        List<Shipment> shipments = new ArrayList<>();
        Shipment shipment = new Shipment();
        UUID shipmentId = UUID.randomUUID();
        shipment.setId(shipmentId);
        shipment.setRequestId(requestId);
        shipment.setName(shipmentDTO.getName());
        shipment.setVolume(shipmentDTO.getVolume());
        shipment.setRevenue(shipmentDTO.getRevenue());
        shipment.setIsSelected(false);
        shipments.add(shipment);

        when(shipmentsService.getShipments(any())).thenReturn(shipments);

        vanLoadRequestsService.load(vanLoadRequestDTO);

        verify(vanLoadRequestsRepository, times(1)).save(
                argThat(request ->
                        request.getMaxVolume().equals(vanLoadRequestDTO.getMaxVolume())
                ));
        verify(shipmentsService, times(1)).saveShipment(any(ShipmentDTO.class), eq(requestId));
        verify(knapsackAlgorithm, times(1)).calculateShipments(eq(vanLoadRequest), eq(shipments));
    }

    @Test
    void getRequest_shouldReturnRequestFromRepository() {
        VanLoadRequest vanLoadRequest = new VanLoadRequest();
        UUID requestId = UUID.randomUUID();
        vanLoadRequest.setId(requestId);
        vanLoadRequest.setMaxVolume(BigDecimal.valueOf(20));
        vanLoadRequest.setTotalVolume(BigDecimal.valueOf(0));
        vanLoadRequest.setTotalRevenue(BigDecimal.valueOf(0));
        vanLoadRequest.setCreatedAt(Instant.now());
        vanLoadRequest.setLoadStatus(LoadStatus.LOADED);

        when(vanLoadRequestsRepository.findById(any())).thenReturn(Optional.of(vanLoadRequest));

        RequestWithShipmentsResponse response = vanLoadRequestsService.getRequest(requestId);

        verify(vanLoadRequestsRepository, times(1)).findById(requestId);

        assertEquals(vanLoadRequest.getId(), response.getRequestId());
        assertEquals(vanLoadRequest.getTotalVolume(), response.getTotalVolume());
        assertEquals(vanLoadRequest.getTotalRevenue(), response.getTotalRevenue());
        assertEquals(vanLoadRequest.getCreatedAt(), response.getCreatedAt());
        assertEquals(vanLoadRequest.getLoadStatus(), response.getLoadStatus());
    }

    @Test
    void filterRequests_shouldReturnFilteredRequests() {
        VanLoadRequest vanLoadRequest1 = new VanLoadRequest();
        UUID requestId1 = UUID.randomUUID();
        vanLoadRequest1.setId(requestId1);
        vanLoadRequest1.setMaxVolume(BigDecimal.valueOf(20));
        vanLoadRequest1.setTotalVolume(BigDecimal.valueOf(10));
        vanLoadRequest1.setTotalRevenue(BigDecimal.valueOf(45));
        vanLoadRequest1.setCreatedAt(Instant.now());
        vanLoadRequest1.setLoadStatus(LoadStatus.CREATED);

        VanLoadRequest vanLoadRequest2 = new VanLoadRequest();
        UUID requestId2 = UUID.randomUUID();
        vanLoadRequest2.setId(requestId2);
        vanLoadRequest2.setMaxVolume(BigDecimal.valueOf(150));
        vanLoadRequest2.setTotalVolume(BigDecimal.valueOf(10));
        vanLoadRequest2.setTotalRevenue(BigDecimal.valueOf(15));
        vanLoadRequest2.setCreatedAt(Instant.now());
        vanLoadRequest2.setLoadStatus(LoadStatus.LOADED);

        List<VanLoadRequest> vanLoadRequests = new ArrayList<>();
        vanLoadRequests.add(vanLoadRequest1);
        vanLoadRequests.add(vanLoadRequest2);

        when(vanLoadRequestsRepository.findFilteredRequests(any(), any(), any(), any(), any())).thenReturn(vanLoadRequests);

        List<RequestWithShipmentsResponse> responses = vanLoadRequestsService.filterRequests(null, null, null, null, null);
        verify(vanLoadRequestsRepository, times(1)).findFilteredRequests(null, null, null, null, null);
        verify(shipmentsService, times(1)).getSelectedShipments(vanLoadRequest1.getId());
        verify(shipmentsService, times(1)).getSelectedShipments(vanLoadRequest2.getId());
    }
}
