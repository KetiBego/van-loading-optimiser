package com.ketevan.vanloadingoptimiser.controller;

import com.ketevan.vanloadingoptimiser.dto.RequestWithShipmentsResponse;
import com.ketevan.vanloadingoptimiser.dto.VanLoadResponse;
import com.ketevan.vanloadingoptimiser.entity.LoadStatus;
import com.ketevan.vanloadingoptimiser.service.VanLoadRequestsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VanLoadRequestsController.class)
@ExtendWith(MockitoExtension.class)
public class VanLoadRequestsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VanLoadRequestsService vanLoadRequestsService;

    @Test
    void load_shouldCreateAndReturn201() throws Exception {
        VanLoadResponse vanLoadResponse = new VanLoadResponse(UUID.randomUUID(), LoadStatus.CREATED);
        when(vanLoadRequestsService.load(any())).thenReturn(vanLoadResponse);

        mockMvc.perform(post("/api/v1/van/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "maxVolume": 15,
                                    "availableShipments": []
                                }
                                """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.loadStatus").value("CREATED"));
    }

    @Test
    void load_shouldThrowErrorWhenInvalidArguments() throws Exception {
        VanLoadResponse vanLoadResponse = new VanLoadResponse(UUID.randomUUID(), LoadStatus.CREATED);
        when(vanLoadRequestsService.load(any())).thenReturn(vanLoadResponse);

        mockMvc.perform(post("/api/v1/van/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "maxVolume": -1,
                                    "availableShipments": []
                                }
                                """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequest_shouldFindAndReturn200() throws Exception {
        UUID uuid = UUID.randomUUID();
        RequestWithShipmentsResponse requestWithShipmentsResponse = new RequestWithShipmentsResponse(
                uuid,
                Collections.emptyList(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                Instant.now(),
                LoadStatus.CREATED
        );
        when(vanLoadRequestsService.getRequest(any())).thenReturn(requestWithShipmentsResponse);

        mockMvc.perform(get("/api/v1/van/request/{id}", uuid)
                        .param("requestId", String.valueOf(uuid))
                )
                .andExpect(status().isOk());
    }

    @Test
    void getRequest_shouldFindAndReturn400WhenInvalidArguments() throws Exception {
        RequestWithShipmentsResponse requestWithShipmentsResponse = new RequestWithShipmentsResponse(
                UUID.randomUUID(),
                Collections.emptyList(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                Instant.now(),
                LoadStatus.CREATED
        );
        when(vanLoadRequestsService.getRequest(any())).thenReturn(requestWithShipmentsResponse);

        mockMvc.perform(get("/api/v1/van/request/{id}", "uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void filterRequests_shouldFindAndReturn200() throws Exception {
        UUID uuid = UUID.randomUUID();
        RequestWithShipmentsResponse requestWithShipmentsResponse = new RequestWithShipmentsResponse(
                uuid,
                Collections.emptyList(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                Instant.now(),
                LoadStatus.CREATED
        );
        List<RequestWithShipmentsResponse> list = Collections.singletonList(requestWithShipmentsResponse);
        when(vanLoadRequestsService.filterRequests(any(), any(), any(), any(), any()))
                .thenReturn(list);

        mockMvc.perform(get("/api/v1/van/filter", uuid)
                        .param("status", "CREATED")
                )
                .andExpect(status().isOk());
    }

    @Test
    void filterRequests_shouldReturn400WhenInvalidArguments() throws Exception {
        UUID uuid = UUID.randomUUID();
        RequestWithShipmentsResponse requestWithShipmentsResponse = new RequestWithShipmentsResponse(
                uuid,
                Collections.emptyList(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                Instant.now(),
                LoadStatus.CREATED
        );
        List<RequestWithShipmentsResponse> list = Collections.singletonList(requestWithShipmentsResponse);
        when(vanLoadRequestsService.filterRequests(any(), any(), any(), any(), any()))
                .thenReturn(list);

        mockMvc.perform(get("/api/v1/van/filter", uuid)
                        .param("status", "C")
                )
                .andExpect(status().isBadRequest());
    }
}
