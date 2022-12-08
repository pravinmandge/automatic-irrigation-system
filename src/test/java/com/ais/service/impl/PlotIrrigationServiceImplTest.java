package com.ais.service.impl;

import com.ais.dto.PlotIrrigationDTO;
import com.ais.dto.PlotIrrigationRequestDTO;
import com.ais.model.Plot;
import com.ais.model.PlotIrrigation;
import com.ais.repository.PlotIrrigationRepository;
import com.ais.repository.PlotRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PlotIrrigationServiceImplTest {

    @Mock
    private PlotIrrigationRepository plotIrrigationRepository;

    @Mock
    private PlotRepository plotRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PlotIrrigationServiceImpl plotIrrigationService;

    @Test
    void testConfigurePlotSuccess() {

        Plot plot = createPlot("plot1");

        PlotIrrigation plotIrrigation = createPlotIrrigation(plot);

        Mockito.when(plotRepository.findById(any())).thenReturn(Optional.of(plot));
        Mockito.when(plotIrrigationRepository.findByPlot_Id(any())).thenReturn(plotIrrigation);
        Mockito.when(plotIrrigationRepository.save(any())).thenReturn(plotIrrigation);
        Mockito.when(modelMapper.map(any(), any())).thenReturn(createPlotIrrigationDTO(plotIrrigation));

        PlotIrrigationDTO plotIrrigationDTO = plotIrrigationService.configurePlot("plotId", createPlotIrrigationRequest());

        Assertions.assertNotNull(plotIrrigationDTO);
    }

    @Test
    void testConfigurePlotSuccessWhenPlotIrrigationNotExist() {

        Plot plot = createPlot("plot1");

        PlotIrrigation plotIrrigation = createPlotIrrigation(plot);

        Mockito.when(plotRepository.findById(any())).thenReturn(Optional.of(plot));
        Mockito.when(plotIrrigationRepository.findByPlot_Id(any())).thenReturn(null);
        Mockito.when(plotIrrigationRepository.save(any())).thenReturn(plotIrrigation);
        Mockito.when(modelMapper.map(any(), any())).thenReturn(createPlotIrrigationDTO(plotIrrigation));

        PlotIrrigationDTO plotIrrigationDTO = plotIrrigationService.configurePlot("plotId", createPlotIrrigationRequest());

        Assertions.assertNotNull(plotIrrigationDTO);
    }

    @Test
    void testSaveAllSuccess() {

        List<PlotIrrigationDTO> plotIrrigationDTOList = createPlotIrrigationDTOList();

        List<PlotIrrigation> plotIrrigationList = createPlotIrrigationList(plotIrrigationDTOList);

        Mockito.when(plotIrrigationRepository.findAllById(any())).thenReturn(plotIrrigationList);
        Mockito.when(plotIrrigationRepository.saveAll(any())).thenReturn(plotIrrigationList);

        List<PlotIrrigationDTO> createdPlotIrrigationDTOList = plotIrrigationService.saveAll(plotIrrigationDTOList);

        Assertions.assertNotNull(createdPlotIrrigationDTOList);
    }

    private List<PlotIrrigationDTO> createPlotIrrigationDTOList(){
        List<PlotIrrigationDTO> plotIrrigationDTOList = new ArrayList<>();

        plotIrrigationDTOList.add(createPlotIrrigationDTO());
        plotIrrigationDTOList.add(createPlotIrrigationDTO());

        return plotIrrigationDTOList;
    }

    private List<PlotIrrigation> createPlotIrrigationList(List<PlotIrrigationDTO> plotIrrigationDTOList){
        return plotIrrigationDTOList.stream().map(this::createPlotIrrigation).collect(Collectors.toList());
    }

    private PlotIrrigation createPlotIrrigation(Plot plot) {
        return PlotIrrigation.builder().plot(plot).id(UUID.randomUUID().toString()).build();
    }

    private PlotIrrigationDTO createPlotIrrigationDTO(PlotIrrigation plotIrrigation) {
        return PlotIrrigationDTO.builder().plotId(plotIrrigation.getPlot().getId()).id(plotIrrigation.getId()).build();
    }

    private PlotIrrigation createPlotIrrigation(PlotIrrigationDTO plotIrrigation) {
        return PlotIrrigation.builder().id(plotIrrigation.getId()).build();
    }

    private PlotIrrigationDTO createPlotIrrigationDTO() {
        return PlotIrrigationDTO.builder().plotId(UUID.randomUUID().toString()).id(UUID.randomUUID().toString()).build();
    }

    private Plot createPlot(String plotName) {
        return Plot.builder().id(UUID.randomUUID().toString())
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
