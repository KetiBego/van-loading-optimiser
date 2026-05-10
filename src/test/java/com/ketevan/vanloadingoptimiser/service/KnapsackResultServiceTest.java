package com.ketevan.vanloadingoptimiser.service;

import com.ketevan.vanloadingoptimiser.entity.LoadStatus;
import com.ketevan.vanloadingoptimiser.entity.Shipment;
import com.ketevan.vanloadingoptimiser.entity.VanLoadRequest;
import com.ketevan.vanloadingoptimiser.repository.ShipmentsRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KnapsackResultServiceTest {
    @Mock
    private ShipmentsRepository shipmentsRepository;
    @Mock
    private VanLoadRequestsRepository vanLoadRequestsRepository;

    @InjectMocks
    private KnapsackResultService knapsackResultService;

    @Test
    void updateDatabase_CallsRepository() {
        VanLoadRequest vanLoadRequest = new VanLoadRequest();
        UUID requestId = UUID.randomUUID();
        vanLoadRequest.setId(requestId);
        vanLoadRequest.setTotalVolume(BigDecimal.valueOf(0));
        vanLoadRequest.setTotalRevenue(BigDecimal.valueOf(0));
        vanLoadRequest.setMaxVolume(BigDecimal.valueOf(15));
        vanLoadRequest.setCreatedAt(Instant.now());
        vanLoadRequest.setLoadStatus(LoadStatus.CREATED);

        List<Shipment> shipments = new ArrayList<>();
        Shipment shipment = new Shipment();
        shipment.setId(UUID.randomUUID());
        shipment.setRequestId(requestId);
        shipment.setName("Shipment");
        shipment.setVolume(BigDecimal.valueOf(5));
        shipment.setRevenue(BigDecimal.valueOf(120));
        shipment.setIsSelected(false);
        shipments.add(shipment);

        when(shipmentsRepository.save(any())).thenReturn(shipment);
        when(vanLoadRequestsRepository.save(any())).thenReturn(vanLoadRequest);

        knapsackResultService.updateDatabase(vanLoadRequest, shipments);
        assertEquals(true, shipment.getIsSelected());

        verify(shipmentsRepository, times(1)).save(argThat(s ->
                s.getRequestId().equals(requestId) &&
                        s.getRevenue().equals(shipment.getRevenue()) &&
                        s.getVolume().equals(shipment.getVolume()) &&
                        s.getIsSelected().equals(true)
        ));

        assertEquals(LoadStatus.LOADED, vanLoadRequest.getLoadStatus());
        assertEquals(BigDecimal.valueOf(5), vanLoadRequest.getTotalVolume());
        assertEquals(BigDecimal.valueOf(120), vanLoadRequest.getTotalRevenue());

        verify(vanLoadRequestsRepository, times(1)).save(vanLoadRequest);
    }
}
