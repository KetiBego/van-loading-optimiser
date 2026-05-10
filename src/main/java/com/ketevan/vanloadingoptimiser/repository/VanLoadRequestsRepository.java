package com.ketevan.vanloadingoptimiser.repository;

import com.ketevan.vanloadingoptimiser.entity.LoadStatus;
import com.ketevan.vanloadingoptimiser.entity.VanLoadRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface VanLoadRequestsRepository extends JpaRepository<VanLoadRequest, UUID> {
    @Query(value = """
            SELECT r FROM van_load_requests r
            WHERE (:loadStatus IS NULL OR r.loadStatus = :loadStatus)
              AND (:minRevenue IS NULL OR r.totalRevenue >= :minRevenue)
              AND (:maxRevenue IS NULL OR r.totalRevenue <= :maxRevenue)
              AND (CAST(:from AS timestamp) IS NULL OR r.createdAt >= :from)
              AND (CAST(:to AS timestamp) IS NULL OR r.createdAt <= :to)
            """)
    List<VanLoadRequest> findFilteredRequests(
            @Param("loadStatus") LoadStatus status,
            @Param("minRevenue") BigDecimal minRevenue,
            @Param("maxRevenue") BigDecimal maxRevenue,
            @Param("from") Instant from,
            @Param("to") Instant to);
}
