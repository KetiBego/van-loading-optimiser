package com.ketevan.vanloadingoptimiser.repository;

import com.ketevan.vanloadingoptimiser.entity.LoadStatus;
import com.ketevan.vanloadingoptimiser.entity.VanLoadRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class VanLoadingRequestsRepositoryTest {

    @Autowired
    private VanLoadRequestsRepository vanLoadRequestsRepository;

    @Test
    void findFilteredRequests_shouldReturnFilteredList() {
        Instant from = Instant.parse("2026-01-01T00:00:00Z");
        Instant to = Instant.parse("2026-12-31T00:00:00Z");

        VanLoadRequest vanLoadRequest1 = new VanLoadRequest();
        vanLoadRequest1.setMaxVolume(BigDecimal.valueOf(20));
        vanLoadRequest1.setTotalVolume(BigDecimal.valueOf(10));
        vanLoadRequest1.setTotalRevenue(BigDecimal.valueOf(45));
        vanLoadRequest1.setCreatedAt(Instant.parse("2026-05-10T00:00:00Z"));
        vanLoadRequest1.setLoadStatus(LoadStatus.CREATED);

        vanLoadRequestsRepository.save(vanLoadRequest1);

        VanLoadRequest vanLoadRequest2 = new VanLoadRequest();
        vanLoadRequest2.setMaxVolume(BigDecimal.valueOf(150));
        vanLoadRequest2.setTotalVolume(BigDecimal.valueOf(10));
        vanLoadRequest2.setTotalRevenue(BigDecimal.valueOf(15));
        vanLoadRequest2.setCreatedAt(Instant.parse("2025-05-10T00:00:00Z"));
        vanLoadRequest2.setLoadStatus(LoadStatus.LOADED);

        vanLoadRequestsRepository.save(vanLoadRequest2);

        List<VanLoadRequest> filteredList = vanLoadRequestsRepository.findFilteredRequests(
                LoadStatus.CREATED,
                BigDecimal.valueOf(30),
                BigDecimal.valueOf(50),
                from,
                to);

        assertEquals(1, filteredList.size());
        assertEquals(BigDecimal.valueOf(45), filteredList.getFirst().getTotalRevenue());
    }
}
