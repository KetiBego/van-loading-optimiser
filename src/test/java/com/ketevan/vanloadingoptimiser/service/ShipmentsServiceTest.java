package com.ketevan.vanloadingoptimiser.service;

import com.ketevan.vanloadingoptimiser.dto.ShipmentDTO;
import com.ketevan.vanloadingoptimiser.entity.Shipment;
import com.ketevan.vanloadingoptimiser.repository.ShipmentsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShipmentsServiceTest {

    @Mock
    private ShipmentsRepository shipmentsRepository;

    @InjectMocks
    private ShipmentsService shipmentsService;

    @Test
    void createShipment_shouldCreateShipmentAndSaveInRepository() {
        Shipment shipment = new Shipment();
        UUID requestId = UUID.randomUUID();
        shipment.setRequestId(requestId);
        shipment.setName("shipment");
        shipment.setVolume(BigDecimal.valueOf(10));
        shipment.setRevenue(BigDecimal.valueOf(20));
        shipment.setIsSelected(false);

        when(shipmentsRepository.save(any(Shipment.class))).thenReturn(shipment);

        ShipmentDTO shipmentDTO = new ShipmentDTO();
        shipmentDTO.setName(shipment.getName());
        shipmentDTO.setVolume(shipment.getVolume());
        shipmentDTO.setRevenue(shipment.getRevenue());
        shipmentsService.saveShipment(shipmentDTO, requestId);

        verify(shipmentsRepository, times(1)).save(shipment);
    }
}
