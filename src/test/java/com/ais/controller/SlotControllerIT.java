package com.ais.controller;

import com.ais.dto.PlotDTO;
import com.ais.dto.SlotDTO;
import com.ais.model.SlotStatus;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@DirtiesContext
public class SlotControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${local.server.port}")
    private int port;

    @BeforeEach
    public void setup() {
        restTemplate
                .getRestTemplate()
                .setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @Test
    @Sql(scripts = {"classpath:data/clearAll.sql", "classpath:data/populatePlotIrrigation.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetAllSlot() {
        System.out.println("Test Get all Slot");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        Awaitility.await().atMost(65, TimeUnit.SECONDS).until(waitForSlotToBeCreated("101"));

        ResponseEntity<List<SlotDTO>> response = restTemplate.exchange("http://localhost:" + port + "/api/v1/slots", HttpMethod.GET, null, new ParameterizedTypeReference<List<SlotDTO>>() {
        });
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        List<SlotDTO> slots = response.getBody();
        assert slots != null;
        Assertions.assertEquals(1, slots.size());

        System.out.println("Completed - Test Get all slot");
    }

    @Test
    @Sql(scripts = {"classpath:data/clearAll.sql", "classpath:data/populatePlotIrrigation.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetAllSlotByPlotId() {
        System.out.println("Test Get all Slot By Plot Id");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        Awaitility.await().atMost(65, TimeUnit.SECONDS).until(waitForSlotToBeCreated("101"));

        ResponseEntity<List<SlotDTO>> response = restTemplate.exchange("http://localhost:" + port + "/api/v1/slots/?plotId=" + "101", HttpMethod.GET, null, new ParameterizedTypeReference<List<SlotDTO>>() {
        });
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        List<SlotDTO> slots = response.getBody();
        assert slots != null;
        Assertions.assertEquals(1, slots.size());

        System.out.println("Completed - Test Get all slot by Plot Id");
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
}
