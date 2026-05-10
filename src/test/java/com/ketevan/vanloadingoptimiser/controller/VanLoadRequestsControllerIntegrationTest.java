package com.ketevan.vanloadingoptimiser.controller;

import com.ketevan.vanloadingoptimiser.configuration.SyncAsyncConfig;
import com.ketevan.vanloadingoptimiser.dto.RequestWithShipmentsResponse;
import com.ketevan.vanloadingoptimiser.dto.ShipmentDTO;
import com.ketevan.vanloadingoptimiser.dto.VanLoadRequestDTO;
import com.ketevan.vanloadingoptimiser.dto.VanLoadResponse;
import com.ketevan.vanloadingoptimiser.entity.LoadStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Import(SyncAsyncConfig.class)
public class VanLoadRequestsControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testAllEndpoints() {
        // first request
        VanLoadRequestDTO vanLoadRequestDTO = new VanLoadRequestDTO();
        vanLoadRequestDTO.setMaxVolume(BigDecimal.valueOf(16));
        List<ShipmentDTO> shipmentDTOList = getShipmentDTOS();
        vanLoadRequestDTO.setAvailableShipments(shipmentDTOList);

        ResponseEntity<VanLoadResponse> response =
                restTemplate.postForEntity(
                        "/api/v1/van/load",
                        vanLoadRequestDTO,
                        VanLoadResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assert response.getBody() != null;

        UUID requestId = response.getBody().getRequestId();
        ResponseEntity<RequestWithShipmentsResponse> requestWithShipmentsResponseResponseEntity =
                restTemplate.getForEntity(
                        "/api/v1/van/request/" + requestId,
                        RequestWithShipmentsResponse.class);

        assertEquals(HttpStatus.OK, requestWithShipmentsResponseResponseEntity.getStatusCode());
        assert requestWithShipmentsResponseResponseEntity.getBody() != null;
        RequestWithShipmentsResponse responseWithShipments = requestWithShipmentsResponseResponseEntity.getBody();
        assertEquals(LoadStatus.LOADED, responseWithShipments.getLoadStatus());
        assertEquals(1, responseWithShipments.getSelectedShipments().size());
        assertEquals(0, BigDecimal.valueOf(35).compareTo(responseWithShipments.getTotalRevenue()));
        assertEquals(0, BigDecimal.valueOf(15).compareTo(responseWithShipments.getTotalVolume()));

        // second request
        vanLoadRequestDTO.setMaxVolume(BigDecimal.valueOf(1));

        response = restTemplate.postForEntity(
                "/api/v1/van/load",
                vanLoadRequestDTO,
                VanLoadResponse.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assert response.getBody() != null;

        requestId = response.getBody().getRequestId();
        requestWithShipmentsResponseResponseEntity =
                restTemplate.getForEntity(
                        "/api/v1/van/request/" + requestId,
                        RequestWithShipmentsResponse.class);

        assertEquals(HttpStatus.OK, requestWithShipmentsResponseResponseEntity.getStatusCode());
        assert requestWithShipmentsResponseResponseEntity.getBody() != null;
        responseWithShipments = requestWithShipmentsResponseResponseEntity.getBody();
        assertEquals(LoadStatus.LOADED, responseWithShipments.getLoadStatus());
        assertEquals(0, responseWithShipments.getSelectedShipments().size());
        assertEquals(0, BigDecimal.valueOf(0).compareTo(responseWithShipments.getTotalRevenue()));
        assertEquals(0, BigDecimal.valueOf(0).compareTo(responseWithShipments.getTotalVolume()));

        // third request
        vanLoadRequestDTO.setMaxVolume(BigDecimal.valueOf(62.7));

        response = restTemplate.postForEntity(
                "/api/v1/van/load",
                vanLoadRequestDTO,
                VanLoadResponse.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assert response.getBody() != null;

        requestId = response.getBody().getRequestId();
        requestWithShipmentsResponseResponseEntity =
                restTemplate.getForEntity(
                        "/api/v1/van/request/" + requestId,
                        RequestWithShipmentsResponse.class);

        assertEquals(HttpStatus.OK, requestWithShipmentsResponseResponseEntity.getStatusCode());
        assert requestWithShipmentsResponseResponseEntity.getBody() != null;
        responseWithShipments = requestWithShipmentsResponseResponseEntity.getBody();
        assertEquals(LoadStatus.LOADED, responseWithShipments.getLoadStatus());
        assertEquals(5, responseWithShipments.getSelectedShipments().size());
        assertEquals(0, BigDecimal.valueOf(135).compareTo(responseWithShipments.getTotalRevenue()));
        assertEquals(0, BigDecimal.valueOf(62.7).compareTo(responseWithShipments.getTotalVolume()));


        // filter test
        ParameterizedTypeReference<List<RequestWithShipmentsResponse>> requestWithShipmentsResponseParameterizedTypeReference = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<List<RequestWithShipmentsResponse>> requestWithShipmentsResponseListResponseEntity =
                restTemplate.exchange(
                        "/api/v1/van/filter?minRevenue=130&maxRevenue=500",
                        HttpMethod.GET,
                        null,
                        requestWithShipmentsResponseParameterizedTypeReference);
        assertEquals(HttpStatus.OK, requestWithShipmentsResponseListResponseEntity.getStatusCode());
        List<RequestWithShipmentsResponse> requestWithShipmentsResponseList = requestWithShipmentsResponseListResponseEntity.getBody();
        assert requestWithShipmentsResponseList != null;
        assertEquals(1, requestWithShipmentsResponseList.size());
        assertEquals(0, BigDecimal.valueOf(135).compareTo(requestWithShipmentsResponseList.getFirst().getTotalRevenue()));
        assertEquals(0, BigDecimal.valueOf(62.7).compareTo(requestWithShipmentsResponseList.getFirst().getTotalVolume()));
    }

    private static List<ShipmentDTO> getShipmentDTOS() {
        List<ShipmentDTO> shipmentDTOList = new ArrayList<>();
        ShipmentDTO shipment1DTO = new ShipmentDTO();
        shipment1DTO.setName("Shipment1");
        shipment1DTO.setVolume(BigDecimal.valueOf(6));
        shipment1DTO.setRevenue(BigDecimal.valueOf(10));
        shipmentDTOList.add(shipment1DTO);

        ShipmentDTO shipment2DTO = new ShipmentDTO();
        shipment2DTO.setName("Shipment2");
        shipment2DTO.setVolume(BigDecimal.valueOf(10));
        shipment2DTO.setRevenue(BigDecimal.valueOf(2));
        shipmentDTOList.add(shipment2DTO);

        ShipmentDTO shipment3DTO = new ShipmentDTO();
        shipment3DTO.setName("Shipment3");
        shipment3DTO.setVolume(BigDecimal.valueOf(15));
        shipment3DTO.setRevenue(BigDecimal.valueOf(35));
        shipmentDTOList.add(shipment3DTO);

        ShipmentDTO shipment4DTO = new ShipmentDTO();
        shipment4DTO.setName("Shipment4");
        shipment4DTO.setVolume(BigDecimal.valueOf(1.7));
        shipment4DTO.setRevenue(BigDecimal.valueOf(18));
        shipmentDTOList.add(shipment4DTO);

        ShipmentDTO shipment5DTO = new ShipmentDTO();
        shipment5DTO.setName("Shipment5");
        shipment5DTO.setVolume(BigDecimal.valueOf(30));
        shipment5DTO.setRevenue(BigDecimal.valueOf(70));
        shipmentDTOList.add(shipment5DTO);

        return shipmentDTOList;
    }
}
