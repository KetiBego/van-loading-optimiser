package com.ketevan.vanloadingoptimiser.service;

import com.ketevan.vanloadingoptimiser.dto.ShipmentDTO;
import com.ketevan.vanloadingoptimiser.entity.Shipment;
import com.ketevan.vanloadingoptimiser.repository.ShipmentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentsService {
    private final ShipmentsRepository shipmentsRepository;

    public void saveShipment(ShipmentDTO shipmentDTO, UUID requestId) {
        Shipment shipment = new Shipment();
        shipment.setRequestId(requestId);
        shipment.setName(shipmentDTO.getName());
        shipment.setVolume(shipmentDTO.getVolume());
        shipment.setRevenue(shipmentDTO.getRevenue());
        shipment.setIsSelected(false);
        shipmentsRepository.save(shipment);
    }

    public List<Shipment> getShipments(UUID requestId) {
        return shipmentsRepository.findAllByRequestId(requestId).orElseThrow();
    }

    public List<ShipmentDTO> getSelectedShipments(UUID requestId) {
        List<Shipment> shipments = shipmentsRepository.findAllByRequestIdAndIsSelected(requestId, true).orElseThrow();
        return shipments.stream().map(this::mapToDto).toList();
    }

    private ShipmentDTO mapToDto(Shipment shipment) {
        ShipmentDTO shipmentDTO = new ShipmentDTO();
        shipmentDTO.setName(shipment.getName());
        shipmentDTO.setVolume(shipment.getVolume());
        shipmentDTO.setRevenue(shipment.getRevenue());
        return shipmentDTO;
    }
}
