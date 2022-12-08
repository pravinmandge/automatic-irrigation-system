package com.ais.service.impl;


import com.ais.dto.PlotDTO;
import com.ais.exception.PlotException;
import com.ais.model.Plot;
import com.ais.repository.PlotRepository;
import com.ais.utils.IrrigationError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PlotServiceImplTest {

    @Mock
    private PlotRepository plotRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PlotServiceImpl plotService;

    @Test
    void testGetAllPlotSuccess() {

        Mockito.when(plotRepository.findAll()).thenReturn(createPlotList());
        List<PlotDTO> plotDTOList = plotService.getAllPlots();

        Assertions.assertNotNull(plotDTOList);
        Assertions.assertEquals(2, plotDTOList.size());
    }

    @Test
    void testGetAllPlotWhenNoPlots() {

        Mockito.when(plotRepository.findAll()).thenReturn(Collections.emptyList());
        List<PlotDTO> plotDTOList = plotService.getAllPlots();

        Assertions.assertNotNull(plotDTOList);
        Assertions.assertEquals(0, plotDTOList.size());
    }

    @Test
    void testGetPlotByIdSuccess() {

        Plot plot = createPlot("plot1");
        PlotDTO plotDTO = createPlotDTO(plot);

        Mockito.when(plotRepository.findById(any())).thenReturn(Optional.ofNullable(plot));
        Mockito.when(modelMapper.map(any(), any())).thenReturn(plotDTO);
        PlotDTO createdPlotDTO = plotService.getPlot(UUID.randomUUID().toString());

        Assertions.assertNotNull(createdPlotDTO);
        Assertions.assertEquals("plot1", createdPlotDTO.getName());
    }

    @Test
    void testGetPlotByIdWhenNoPlotExist() {

        Mockito.when(plotRepository.findById(any())).thenReturn(Optional.empty());
        try {
            plotService.getPlot(UUID.randomUUID().toString());
        } catch (PlotException e) {
            Assertions.assertEquals(IrrigationError.PLOT_NOT_FOUND, e.getIrrigationError());
        }
    }

    @Test
    void testCreatePlotSuccess() {

        Plot plot = createPlot("plot1");
        PlotDTO plotDTO = createPlotDTO(plot);

        Mockito.when(plotRepository.save(any())).thenReturn(plot);
        Mockito.when(modelMapper.map(any(), any())).thenReturn(plotDTO);
        PlotDTO createdPlotDTO = plotService.addPlot(plotDTO);

        Assertions.assertNotNull(createdPlotDTO);
        Assertions.assertEquals("plot1", createdPlotDTO.getName());
    }

    public List<Plot> createPlotList() {
        List<Plot> plotList = new ArrayList<>();
        plotList.add(createPlot("Plot 1"));
        plotList.add(createPlot("Plot 2"));

        return plotList;
    }

    private Plot createPlot(String plotName) {
        return Plot.builder().id(UUID.randomUUID().toString())
                .name(plotName)
                .sensorUrl("url")
                .build();
    }

    private PlotDTO createPlotDTO(Plot plot) {
        return PlotDTO.builder().id(plot.getId())
                .name(plot.getName())
                .sensorUrl(plot.getSensorUrl())
                .build();
    }
}
