package com.ais.controller;

import com.ais.dto.PlotDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class PlotControllerIT {

    @Autowired private TestRestTemplate restTemplate;

    @Autowired private ModelMapper modelMapper;

    @Value("${local.server.port}")
    private int port;

    @BeforeEach
    public void setup() {
        restTemplate
                .getRestTemplate()
                .setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @Test
    public void testGetPlot(){
        System.out.println("Test Get a Plot");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        PlotDTO plotDTO = createPlotDTO();

        // Add a Plot
        ResponseEntity<Object> result = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/plots",plotDTO, Object.class);
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());

        PlotDTO plotCreated = modelMapper.map(result.getBody(), PlotDTO.class);

        // Get Plot by Id
        ResponseEntity<Object> response = restTemplate.getForEntity("http://localhost:" + port + "/api/v1/plots/" + plotCreated.getId(), Object.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        PlotDTO plotResponse = modelMapper.map(response.getBody(), PlotDTO.class);
        Assertions.assertEquals(plotCreated, plotResponse);

        System.out.println("Completed - Test Get a plot");
    }

    @Test
    @Sql(scripts = "classpath:/data/clearAll.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetAllPlot(){
        System.out.println("Test Get all Plots");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        PlotDTO plotDTO = createPlotDTO();

        // Add a Plot
        ResponseEntity<Object> result = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/plots",plotDTO, Object.class);
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());

        // Add a Plot
        plotDTO.setName("plot2");
        result = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/plots",plotDTO, Object.class);
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());

        // Get all Plots
        ResponseEntity<List<PlotDTO>> response = restTemplate.exchange("http://localhost:" + port + "/api/v1/plots/", HttpMethod.GET, null, new ParameterizedTypeReference<List<PlotDTO>>() {
        });
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        List<PlotDTO> plots = response.getBody();
        assert plots != null;

        Assertions.assertTrue(plots.size() == 2);

        System.out.println("Completed - Test Get all plots");
    }

    @Test
    public void testUpdatePlot(){
        System.out.println("Test Update a Plot");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        PlotDTO plotDTO = createPlotDTO();

        // Add a Plot
        ResponseEntity<Object> result = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/plots",plotDTO, Object.class);
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());

        PlotDTO plotCreated = modelMapper.map(result.getBody(), PlotDTO.class);

        //update the plot
        plotCreated.setName("plot2");
        HttpEntity<PlotDTO> requestEntity = new HttpEntity<>(plotCreated);
        ResponseEntity<PlotDTO> updateResponse = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/plots/" + plotCreated.getId(), HttpMethod.PUT, requestEntity, PlotDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        Assertions.assertEquals(plotCreated.getName(), updateResponse.getBody().getName());

        // Get Plot by Id
        ResponseEntity<Object> response = restTemplate.getForEntity("http://localhost:" + port + "/api/v1/plots/" + plotCreated.getId(), Object.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        PlotDTO plotResponse = modelMapper.map(response.getBody(), PlotDTO.class);
        Assertions.assertEquals(plotCreated, plotResponse);

        System.out.println("Completed - Test Update a plot");
    }

    private PlotDTO createPlotDTO(){
        PlotDTO plotDTO = new PlotDTO();
        plotDTO.setName("plot1");
        return plotDTO;
    }

}
