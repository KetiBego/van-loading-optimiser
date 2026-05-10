package com.ketevan.vanloadingoptimiser.algorithm;

import com.ketevan.vanloadingoptimiser.entity.LoadStatus;
import com.ketevan.vanloadingoptimiser.entity.Shipment;
import com.ketevan.vanloadingoptimiser.entity.VanLoadRequest;
import com.ketevan.vanloadingoptimiser.repository.VanLoadRequestsRepository;
import com.ketevan.vanloadingoptimiser.service.KnapsackResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KnapsackAlgorithm {

    private final VanLoadRequestsRepository vanLoadRequestsRepository;
    private final KnapsackResultService knapsackResultService;

    @Async
    public void calculateShipments(VanLoadRequest vanLoadRequest, List<Shipment> shipments) {
        vanLoadRequest.setLoadStatus(LoadStatus.LOADING);
        vanLoadRequestsRepository.save(vanLoadRequest);

        List<Shipment> selectedShipmentsFinal = new ArrayList<>();
        List<Shipment> currentShipments = new ArrayList<>();
        double maxRevenue = 0;
        double currentRevenue = 0;
        calculateRec(shipments, currentShipments, 0, vanLoadRequest.getMaxVolume().doubleValue(), currentRevenue, maxRevenue, selectedShipmentsFinal);
        knapsackResultService.updateDatabase(vanLoadRequest, selectedShipmentsFinal);
    }

    private double calculateRec(
            List<Shipment> shipments,
            List<Shipment> currentShipments,
            int index,
            double maxVolume,
            double currentRevenue,
            double maxRevenue,
            List<Shipment> selectedShipmentsFinal) {

        if (index >= shipments.size() || maxVolume <= 0) {
            if (currentRevenue > maxRevenue) {
                selectedShipmentsFinal.clear();
                selectedShipmentsFinal.addAll(currentShipments);
                maxRevenue = currentRevenue;
            }
            return maxRevenue;
        }

        maxRevenue = calculateRec(
                shipments,
                currentShipments,
                index + 1,
                maxVolume,
                currentRevenue,
                maxRevenue,
                selectedShipmentsFinal);
        double volume = shipments.get(index).getVolume().doubleValue();
        if (volume <= maxVolume) {
            double revenue = shipments.get(index).getRevenue().doubleValue();
            currentShipments.add(shipments.get(index));
            maxRevenue = calculateRec(
                    shipments,
                    currentShipments,
                    index + 1,
                    maxVolume - volume,
                    currentRevenue + revenue,
                    maxRevenue,
                    selectedShipmentsFinal);
            currentShipments.removeLast();
        }

        return maxRevenue;
    }
}
