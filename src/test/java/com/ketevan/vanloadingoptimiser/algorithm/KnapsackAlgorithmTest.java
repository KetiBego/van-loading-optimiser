package com.ketevan.vanloadingoptimiser.algorithm;

import com.ketevan.vanloadingoptimiser.entity.LoadStatus;
import com.ketevan.vanloadingoptimiser.entity.Shipment;
import com.ketevan.vanloadingoptimiser.entity.VanLoadRequest;
import com.ketevan.vanloadingoptimiser.repository.VanLoadRequestsRepository;
import com.ketevan.vanloadingoptimiser.service.KnapsackResultService;
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

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KnapsackAlgorithmTest {

    @Mock
    private VanLoadRequestsRepository vanLoadRequestsRepository;
    @Mock
    private KnapsackResultService knapsackResultService;

    @InjectMocks
    private KnapsackAlgorithm knapsackAlgorithm;

    @Test
    void calculateShipments_shipmentsFit() {
        VanLoadRequest vanLoadRequest = createVanLoadRequest();

        List<Shipment> shipments = createShipmentsThatFit(vanLoadRequest.getId());

        knapsackAlgorithm.calculateShipments(vanLoadRequest, shipments);

        verify(knapsackResultService, times(1)).updateDatabase(any(), argThat(list ->
                list.size() == 2 &&
                        list.stream().map(Shipment::getVolume).reduce(BigDecimal.ZERO, BigDecimal::add).equals(BigDecimal.valueOf(15)) &&
                        list.stream().map(Shipment::getRevenue).reduce(BigDecimal.ZERO, BigDecimal::add).equals(BigDecimal.valueOf(320))
        ));
    }

    @Test
    void calculateShipments_shipmentsDontFit() {
        VanLoadRequest vanLoadRequest = createVanLoadRequest();

        List<Shipment> shipments = createShipmentsThatDontFit(vanLoadRequest.getId());

        knapsackAlgorithm.calculateShipments(vanLoadRequest, shipments);

        verify(knapsackResultService, times(1)).updateDatabase(any(), argThat(List::isEmpty));
    }

    private VanLoadRequest createVanLoadRequest() {
        VanLoadRequest vanLoadRequest = new VanLoadRequest();
        UUID requestId = UUID.randomUUID();
        vanLoadRequest.setId(requestId);
        vanLoadRequest.setTotalVolume(BigDecimal.valueOf(0));
        vanLoadRequest.setTotalRevenue(BigDecimal.valueOf(0));
        vanLoadRequest.setMaxVolume(BigDecimal.valueOf(15));
        vanLoadRequest.setCreatedAt(Instant.now());
        vanLoadRequest.setLoadStatus(LoadStatus.CREATED);

        return vanLoadRequest;
    }

    private List<Shipment> createShipmentsThatFit(UUID requestId) {
        List<Shipment> shipments = new ArrayList<>();

        Shipment shipmentA = new Shipment();
        shipmentA.setId(UUID.randomUUID());
        shipmentA.setRequestId(requestId);
        shipmentA.setName("ShipmentA");
        shipmentA.setVolume(BigDecimal.valueOf(5));
        shipmentA.setRevenue(BigDecimal.valueOf(120));
        shipmentA.setIsSelected(false);
        shipments.add(shipmentA);

        Shipment shipmentB = new Shipment();
        shipmentB.setId(UUID.randomUUID());
        shipmentB.setRequestId(requestId);
        shipmentB.setName("ShipmentB");
        shipmentB.setVolume(BigDecimal.valueOf(10));
        shipmentB.setRevenue(BigDecimal.valueOf(200));
        shipmentB.setIsSelected(false);
        shipments.add(shipmentB);

        Shipment shipmentC = new Shipment();
        shipmentC.setId(UUID.randomUUID());
        shipmentC.setRequestId(requestId);
        shipmentC.setName("ShipmentC");
        shipmentC.setVolume(BigDecimal.valueOf(3));
        shipmentC.setRevenue(BigDecimal.valueOf(80));
        shipmentC.setIsSelected(false);
        shipments.add(shipmentC);

        Shipment shipmentD = new Shipment();
        shipmentD.setId(UUID.randomUUID());
        shipmentD.setRequestId(requestId);
        shipmentD.setName("ShipmentD");
        shipmentD.setVolume(BigDecimal.valueOf(8));
        shipmentD.setRevenue(BigDecimal.valueOf(160));
        shipmentD.setIsSelected(false);
        shipments.add(shipmentD);

        return shipments;
    }

    private List<Shipment> createShipmentsThatDontFit(UUID requestId) {
        List<Shipment> shipments = new ArrayList<>();

        Shipment shipmentA = new Shipment();
        shipmentA.setId(UUID.randomUUID());
        shipmentA.setRequestId(requestId);
        shipmentA.setName("ShipmentA");
        shipmentA.setVolume(BigDecimal.valueOf(500));
        shipmentA.setRevenue(BigDecimal.valueOf(120));
        shipmentA.setIsSelected(false);
        shipments.add(shipmentA);

        Shipment shipmentB = new Shipment();
        shipmentB.setId(UUID.randomUUID());
        shipmentB.setRequestId(requestId);
        shipmentB.setName("ShipmentB");
        shipmentB.setVolume(BigDecimal.valueOf(100));
        shipmentB.setRevenue(BigDecimal.valueOf(200));
        shipmentB.setIsSelected(false);
        shipments.add(shipmentB);

        return shipments;
    }
}
