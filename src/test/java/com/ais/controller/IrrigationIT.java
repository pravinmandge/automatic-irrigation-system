package com.ais.controller;

import com.ais.dto.PlotDTO;
import com.ais.dto.PlotIrrigationRequestDTO;
import com.ais.dto.SlotDTO;
import com.ais.model.SlotStatus;
import com.ais.service.SlotService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class IrrigationIT {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired private ModelMapper modelMapper;

    @Autowired private SlotService slotService;

    @Value("${local.server.port}")
    private int port;

    @BeforeEach
    public void setup() {
        restTemplate
                .getRestTemplate()
                .setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @Test
    @Sql(scripts = "classpath:/data/clearAll.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testAutoIrrigation(){
        System.out.println("Test Auto Irrigation");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        // Add a Plot
        PlotDTO plotDTO = createPlotDTO();
        ResponseEntity<Object> plotResult = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/plots",plotDTO, Object.class);
        Assertions.assertEquals(HttpStatus.CREATED, plotResult.getStatusCode());

        PlotDTO plotCreated = modelMapper.map(plotResult.getBody(), PlotDTO.class);

        // Add a Slot
        PlotIrrigationRequestDTO plotIrrigationRequestDTO = createPlotIrrigationRequestDTO();
        restTemplate.put("http://localhost:" + port + "/api/v1/plots/" + plotCreated.getId() + "/configure", plotIrrigationRequestDTO, Object.class);

        Awaitility.await().atMost(65, TimeUnit.SECONDS).until(waitForSlotToBeCreated(plotCreated.getId()));

        Awaitility.await().atMost(120, TimeUnit.SECONDS).until(waitForStatusChange(plotCreated.getId()));

        ResponseEntity<List<SlotDTO>> response = restTemplate.exchange("http://localhost:" + port + "/api/v1/slots", HttpMethod.GET, null, new ParameterizedTypeReference<List<SlotDTO>>() {
        });
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        List<SlotDTO> slots = response.getBody();

        assert slots != null;
        Assertions.assertTrue(slots.size() == 1);

        Assertions.assertEquals(SlotStatus.COMPLETED, slots.get(0).getStatus());

        System.out.println("Completed Test Auto Irrigation");
    }

    private Callable<Boolean> waitForStatusChange(String plotId) {
        return () -> {
            List<SlotDTO> slots = slotService.getAllSlotsByPlotId(plotId);
            if (!slots.isEmpty() && slots.get(0).getStatus() == SlotStatus.COMPLETED) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        };
    }

    private Callable<Boolean> waitForSlotToBeCreated(String plotId) {
        return () -> {

            ResponseEntity<Object> response = restTemplate.exchange("http://localhost:" + port + "/api/v1/slots?plotId=" + plotId, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
            });
            if (response.getStatusCode() != HttpStatus.OK) {
                return Boolean.FALSE;
            }

            List<SlotDTO> slots = modelMapper.map(response.getBody(), new TypeToken<List<SlotDTO>>() {}.getType());

            if (!slots.isEmpty()) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        };
    }

    private PlotDTO createPlotDTO(){
        PlotDTO plotDTO = new PlotDTO();
        plotDTO.setName("plot1");
        plotDTO.setSensorUrl("http://localhost:1080/api/v1/sensors");
        return plotDTO;
    }

    private PlotIrrigationRequestDTO createPlotIrrigationRequestDTO(){
        PlotIrrigationRequestDTO plotIrrigationRequestDTO = new PlotIrrigationRequestDTO();
        plotIrrigationRequestDTO.setWaterNeededInMm(100);
        plotIrrigationRequestDTO.setDurationInMin(1);
        plotIrrigationRequestDTO.setIntervalInMin(1);
        return plotIrrigationRequestDTO;
    }
}
