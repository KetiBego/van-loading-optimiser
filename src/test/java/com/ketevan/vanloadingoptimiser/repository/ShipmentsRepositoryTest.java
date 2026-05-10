package com.ketevan.vanloadingoptimiser.repository;

import com.ketevan.vanloadingoptimiser.entity.Shipment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ShipmentsRepositoryTest {

    @Autowired
    private ShipmentsRepository shipmentsRepository;

    @Test
    void save_shouldPersistShipment() {
        Shipment shipment = new Shipment();
        UUID requestId = UUID.randomUUID();
        shipment.setRequestId(requestId);
        shipment.setName("Shipment");
        shipment.setVolume(BigDecimal.valueOf(10));
        shipment.setRevenue(BigDecimal.valueOf(20));
        shipment.setIsSelected(false);

        Shipment saved = shipmentsRepository.save(shipment);

        assertNotNull(saved.getId());
        assertEquals(shipment.getRequestId(), saved.getRequestId());
        assertEquals(shipment.getName(), saved.getName());
        assertEquals(shipment.getVolume(), saved.getVolume());
        assertEquals(shipment.getRevenue(), saved.getRevenue());
    }

    @Test
    void findAllByRequestId_shouldReturnShipments() {
        UUID requestId = UUID.randomUUID();

        Shipment shipment = new Shipment();
        shipment.setRequestId(requestId);
        shipment.setName("Shipment1");
        shipment.setVolume(BigDecimal.valueOf(10));
        shipment.setRevenue(BigDecimal.valueOf(20));
        shipment.setIsSelected(false);

        shipmentsRepository.save(shipment);

        List<Shipment> shipments = shipmentsRepository.findAllByRequestId(requestId).orElse(null);
        assertNotNull(shipments);
        assertEquals(1, shipments.size());
        assertEquals(shipment.getName(), shipments.getFirst().getName());
        assertEquals(shipment.getVolume(), shipments.getFirst().getVolume());
        assertEquals(shipment.getRevenue(), shipments.getFirst().getRevenue());
    }

    @Test
    void findAllByRequestIdAndIsSelected_shouldReturnShipments() {
        UUID requestId = UUID.randomUUID();

        Shipment shipment1 = new Shipment();
        shipment1.setRequestId(requestId);
        shipment1.setName("Shipment1");
        shipment1.setVolume(BigDecimal.valueOf(10));
        shipment1.setRevenue(BigDecimal.valueOf(20));
        shipment1.setIsSelected(false);

        shipmentsRepository.save(shipment1);

        Shipment shipment2 = new Shipment();
        shipment2.setRequestId(requestId);
        shipment2.setName("Shipment2");
        shipment2.setVolume(BigDecimal.valueOf(15));
        shipment2.setRevenue(BigDecimal.valueOf(25));
        shipment2.setIsSelected(true);

        shipmentsRepository.save(shipment2);

        List<Shipment> shipments = shipmentsRepository.findAllByRequestIdAndIsSelected(requestId, true).orElse(null);
        assertNotNull(shipments);
        assertEquals(1, shipments.size());
        assertEquals(shipment2.getName(), shipments.getFirst().getName());
        assertEquals(shipment2.getVolume(), shipments.getFirst().getVolume());
        assertEquals(shipment2.getRevenue(), shipments.getFirst().getRevenue());
    }
}
