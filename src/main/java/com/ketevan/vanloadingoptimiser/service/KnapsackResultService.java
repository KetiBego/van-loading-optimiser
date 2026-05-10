package com.ketevan.vanloadingoptimiser.service;

import com.ketevan.vanloadingoptimiser.entity.LoadStatus;
import com.ketevan.vanloadingoptimiser.entity.Shipment;
import com.ketevan.vanloadingoptimiser.entity.VanLoadRequest;
import com.ketevan.vanloadingoptimiser.repository.ShipmentsRepository;
import com.ketevan.vanloadingoptimiser.repository.VanLoadRequestsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KnapsackResultService {

    private final ShipmentsRepository shipmentsRepository;
    private final VanLoadRequestsRepository vanLoadRequestsRepository;

    @Transactional
    public void updateDatabase(VanLoadRequest vanLoadRequest, List<Shipment> selectedShipments) {
        selectedShipments.forEach(shipment -> {
            shipment.setIsSelected(true);
            shipmentsRepository.save(shipment);
        });

        vanLoadRequest.setLoadStatus(LoadStatus.LOADED);
        vanLoadRequest.setTotalVolume(selectedShipments.stream().map(Shipment::getVolume).reduce(BigDecimal.ZERO, BigDecimal::add));
        vanLoadRequest.setTotalRevenue(selectedShipments.stream().map(Shipment::getRevenue).reduce(BigDecimal.ZERO, BigDecimal::add));
        vanLoadRequestsRepository.save(vanLoadRequest);
    }
}
