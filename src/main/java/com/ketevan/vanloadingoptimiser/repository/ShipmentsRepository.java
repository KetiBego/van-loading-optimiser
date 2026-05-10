package com.ketevan.vanloadingoptimiser.repository;

import com.ketevan.vanloadingoptimiser.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShipmentsRepository extends JpaRepository<Shipment, UUID> {
    Optional<List<Shipment>> findAllByRequestId(UUID requestId);
    Optional<List<Shipment>> findAllByRequestIdAndIsSelected(UUID requestId, Boolean isSelected);
}
