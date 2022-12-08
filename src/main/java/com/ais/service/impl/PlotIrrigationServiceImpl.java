package com.ais.service.impl;

import com.ais.dto.PlotIrrigationDTO;
import com.ais.dto.PlotIrrigationRequestDTO;
import com.ais.exception.PlotException;
import com.ais.exception.PlotIrrigationException;
import com.ais.model.IrrigationStatus;
import com.ais.model.Plot;
import com.ais.model.PlotIrrigation;
import com.ais.repository.PlotIrrigationRepository;
import com.ais.repository.PlotRepository;
import com.ais.service.PlotIrrigationService;
import com.ais.utils.IrrigationError;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PlotIrrigationServiceImpl implements PlotIrrigationService {

    private final PlotIrrigationRepository plotIrrigationRepository;
    private final PlotRepository plotRepository;
    private final ModelMapper modelMapper;

    public PlotIrrigationServiceImpl(PlotIrrigationRepository plotIrrigationRepository, PlotRepository plotRepository, ModelMapper modelMapper) {
        this.plotIrrigationRepository = plotIrrigationRepository;
        this.plotRepository = plotRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PlotIrrigationDTO configurePlot(String plotId, PlotIrrigationRequestDTO plotIrrigationRequest) {
        Optional<Plot> optionalPlot = plotRepository.findById(plotId);
        if (optionalPlot.isEmpty())
            throw new PlotException(IrrigationError.PLOT_NOT_FOUND);

        PlotIrrigation plotIrrigation = plotIrrigationRepository.findByPlot_Id(plotId);
        if (plotIrrigation == null) {
            plotIrrigation = new PlotIrrigation();
            plotIrrigation.setId(UUID.randomUUID().toString());
            plotIrrigation.setCreatedTime(System.currentTimeMillis());
            plotIrrigation.setNextIrrigationTime(0L);
            plotIrrigation.setStatus(IrrigationStatus.IDLE);
        }

        plotIrrigation.setPlot(optionalPlot.get());
        plotIrrigation.setUpdatedTime(System.currentTimeMillis());
        plotIrrigation.setDurationInMin(plotIrrigationRequest.getDurationInMin());
        plotIrrigation.setIntervalInMin(plotIrrigationRequest.getIntervalInMin());
        plotIrrigation.setWaterNeededInMm(plotIrrigationRequest.getWaterNeededInMm());
        plotIrrigation = plotIrrigationRepository.save(plotIrrigation);

        return convertToDTO(plotIrrigation);
    }

    @Override
    public List<PlotIrrigationDTO> saveAll(List<PlotIrrigationDTO> plotIrrigationDTOList) {
        // skip saving if list is empty
        if(plotIrrigationDTOList.isEmpty()){
            return plotIrrigationDTOList;
        }

        Map<String, PlotIrrigationDTO> idToPlotIrrigationDto = plotIrrigationDTOList.stream().collect(
                Collectors.toMap(PlotIrrigationDTO::getId, x -> x));

        List<PlotIrrigation> plotIrrigationList = plotIrrigationRepository.findAllById(idToPlotIrrigationDto.keySet());

        plotIrrigationList.forEach(plotIrrigation -> {
            PlotIrrigationDTO plotIrrigationDTO = idToPlotIrrigationDto.get(plotIrrigation.getId());
            plotIrrigation.setUpdatedTime(plotIrrigationDTO.getUpdatedTime());
            plotIrrigation.setNextIrrigationTime(plotIrrigationDTO.getNextIrrigationTime());
            plotIrrigation.setWaterNeededInMm(plotIrrigationDTO.getWaterNeededInMm());
            plotIrrigation.setStatus(plotIrrigationDTO.getStatus());
        });

        plotIrrigationRepository.saveAll(plotIrrigationList);

        return plotIrrigationDTOList;
    }

    @Override
    public PlotIrrigationDTO getPlotIrrigationByPlotId(String plotId) throws PlotIrrigationException {
        log.info("Fetching plot irrigation with plot id: {}", plotId);
        PlotIrrigation plotIrrigation = plotIrrigationRepository.findByPlot_Id(plotId);
        if (plotIrrigation != null) {
            return convertToDTO(plotIrrigation);
        } else {
            log.error("Plot irrigation not found for plot id {}", plotId);
            throw new PlotIrrigationException(IrrigationError.PLOT_IRRIGATION_NOT_FOUND);
        }
    }

    @Override
    public List<PlotIrrigationDTO> findAllPlotIrrigationToCreateSlots(Long currentTime) {
        return plotIrrigationRepository.findAllPlotIrrigationToCreateSlots(currentTime).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private PlotIrrigationDTO convertToDTO(PlotIrrigation plotIrrigation) {
        PlotIrrigationDTO plotIrrigationDTO = modelMapper.map(plotIrrigation, PlotIrrigationDTO.class);
        plotIrrigationDTO.setPlotId(plotIrrigation.getPlot().getId());
        return plotIrrigationDTO;
    }
}
