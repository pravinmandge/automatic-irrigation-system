package com.ais.controller;

import com.ais.dto.PlotDTO;
import com.ais.dto.PlotIrrigationDTO;
import com.ais.dto.PlotIrrigationRequestDTO;
import com.ais.exception.PlotException;
import com.ais.service.PlotIrrigationService;
import com.ais.service.PlotService;
import com.ais.utils.IrrigationError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PlotControllerTest {

    @Mock
    private PlotService plotService;

    @Mock
    private PlotIrrigationService plotIrrigationService;

    @InjectMocks
    private PlotController plotController;

    @Test
    void testGetAllPlotSuccess() {

        Mockito.when(plotService.getAllPlots()).thenReturn(createPlotDTOList());
        ResponseEntity<List<PlotDTO>> response = plotController.getAllPlots();

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetAllPlotWhenNoPlots() {

        Mockito.when(plotService.getAllPlots()).thenReturn(null);
        ResponseEntity response = plotController.getAllPlots();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetPlotByIdSuccess() {

        Mockito.when(plotService.getPlot(any())).thenReturn(createPlotDTO("plot1"));
        ResponseEntity<PlotDTO> response = plotController.getPlot(UUID.randomUUID().toString());

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("plot1", response.getBody().getName());
    }

    @Test
    void testGetPlotByIdWhenNoPlotExist() {

        Mockito.when(plotService.getPlot(any())).thenReturn(null);
        ResponseEntity response = plotController.getPlot(UUID.randomUUID().toString());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetPlotByIdWhenException() {

        Mockito.when(plotService.getPlot(any())).thenThrow(new PlotException(IrrigationError.PLOT_NOT_FOUND));
        try {
            plotController.getPlot(UUID.randomUUID().toString());
        } catch (PlotException e) {
            Assertions.assertEquals(IrrigationError.PLOT_NOT_FOUND, e.getIrrigationError());
        }
    }

    @Test
    void testCreatePlotSuccess() {

        PlotDTO plotDTO = createPlotDTO("plot1");

        Mockito.when(plotService.addPlot(any())).thenReturn(plotDTO);
        ResponseEntity<PlotDTO> response = plotController.createPlot(plotDTO);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("plot1", response.getBody().getName());
    }

    @Test
    void testCreatePlotFail() {

        PlotDTO plotDTO = createPlotDTO("plot1");

        Mockito.when(plotService.addPlot(any())).thenReturn(null);
        try {
            plotController.createPlot(plotDTO);
        } catch (PlotException e) {
            Assertions.assertEquals(IrrigationError.CREATE_PLOT_FAILED, e.getIrrigationError());
        }
    }

    @Test
    void testConfigurePlotSuccess() {

        PlotIrrigationRequestDTO plotIrrigationRequestDTO = createPlotIrrigationRequest();

        PlotIrrigationDTO plotIrrigationDTO = new PlotIrrigationDTO();
        plotIrrigationDTO.setPlotId("plotId");

        Mockito.when(plotIrrigationService.configurePlot(any(), any())).thenReturn(plotIrrigationDTO);
        ResponseEntity<PlotIrrigationDTO> response = plotController.configurePlot("plotId", plotIrrigationRequestDTO);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("plotId", response.getBody().getPlotId());
    }

    @Test
    void testUpdatePlotSuccess() {

        PlotDTO plotDTO = createPlotDTO("plot1");

        Mockito.when(plotService.updatePlot(any(), any())).thenReturn(plotDTO);
        ResponseEntity<PlotDTO> response = plotController.updatePlot("plotId", plotDTO);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("plot1", response.getBody().getName());
    }

    @Test
    void testUpdatePlotFail() {

        PlotDTO plotDTO = createPlotDTO("plot1");

        Mockito.when(plotService.updatePlot(any(), any())).thenThrow(new PlotException(IrrigationError.UPDATE_PLOT_FAILED));
        try {
            plotController.updatePlot("plotId", plotDTO);
        } catch (PlotException e) {
            Assertions.assertEquals(IrrigationError.UPDATE_PLOT_FAILED, e.getIrrigationError());
        }
    }

    public List<PlotDTO> createPlotDTOList() {
        List<PlotDTO> plotDTOList = new ArrayList<>();
        plotDTOList.add(createPlotDTO("Plot 1"));
        plotDTOList.add(createPlotDTO("Plot 2"));

        return plotDTOList;
    }

    private PlotDTO createPlotDTO(String plotName) {
        return PlotDTO.builder().id(UUID.randomUUID().toString())
                .name(plotName)
                .sensorUrl("url")
                .build();
    }

    private PlotIrrigationRequestDTO createPlotIrrigationRequest() {
        return PlotIrrigationRequestDTO.builder()
                .waterNeededInMm(50)
                .durationInMin(2)
                .intervalInMin(2)
                .build();
    }
}
