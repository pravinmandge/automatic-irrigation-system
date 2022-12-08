package com.ais.service.impl;

import com.ais.dto.PlotDTO;
import com.ais.exception.PlotException;
import com.ais.model.Plot;
import com.ais.model.PlotIrrigation;
import com.ais.model.Slot;
import com.ais.repository.PlotIrrigationRepository;
import com.ais.repository.PlotRepository;
import com.ais.repository.SlotRepository;
import com.ais.service.PlotService;
import com.ais.utils.IrrigationError;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PlotServiceImpl implements PlotService {

    private final PlotRepository plotRepository;
    private final SlotRepository slotRepository;
    private final PlotIrrigationRepository plotIrrigationRepository;
    private final ModelMapper modelMapper;

    public PlotServiceImpl(PlotRepository plotRepository, SlotRepository slotRepository, PlotIrrigationRepository plotIrrigationRepository, ModelMapper modelMapper) {
        this.plotRepository = plotRepository;
        this.slotRepository = slotRepository;
        this.plotIrrigationRepository = plotIrrigationRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PlotDTO addPlot(PlotDTO plotDTO) {
        log.info("Creating and adding a new plot.");
        Plot plot = Plot.builder()
                .id(UUID.randomUUID().toString())
                .name(plotDTO.getName())
                .sensorUrl(plotDTO.getSensorUrl())
                .build();

        plotRepository.save(plot);

        log.info("New Plot created with id {}", plot.getId());

        return convertToDTO(plot);
    }

    @Override
    public PlotDTO updatePlot(String id, PlotDTO plotDTO) throws PlotException {
        log.info("Updating plot with id: {}", id);
        Optional<Plot> optionalPlot = plotRepository.findById(id);
        if (optionalPlot.isPresent()) {
            Plot plot = optionalPlot.get();
            plot.setId(plotDTO.getId());
            plot.setName(plotDTO.getName());
            plot.setSensorUrl(plotDTO.getSensorUrl());
            plot.setSlots(fetchSlotsByIds(plotDTO.getSlotIds()));
            if(StringUtils.hasLength(plotDTO.getPlotIrrigationId())) {
                plot.setPlotIrrigation(fetchPlotIrrigationById(plotDTO.getPlotIrrigationId()));
            }
            plotRepository.save(plot);
        } else {
            log.error("Plot not found for id {}", id);
            throw new PlotException(IrrigationError.PLOT_NOT_FOUND);
        }
        return plotDTO;
    }

    @Override
    public PlotDTO getPlot(String id) throws PlotException {
        log.info("Fetching plot with id: {}", id);
        Optional<Plot> plot = plotRepository.findById(id);
        if (plot.isPresent()) {
            return convertToDTO(plot.get());
        } else {
            log.error("Plot not found for id {}", id);
            throw new PlotException(IrrigationError.PLOT_NOT_FOUND);
        }
    }

    @Override
    public List<PlotDTO> getAllPlots() {
        log.info("Fetching all the plots");
        return plotRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private Set<Slot> fetchSlotsByIds(Set<String> slotIds) {
        Set<Slot> slots = new HashSet<>();
        if (slotIds != null) {
            for (String slotId : slotIds) {
                Optional<Slot> optionalSlot = slotRepository.findById(slotId);
                if (optionalSlot.isPresent()) {
                    slots.add(optionalSlot.get());
                } else {
                    log.error("Slot not found for id {}", slotId);
                }
            }
        }
        return slots;
    }

    private PlotIrrigation fetchPlotIrrigationById(String plotIrrigationId) {
        Optional<PlotIrrigation> optionalPlotIrrigation = plotIrrigationRepository.findById(plotIrrigationId);
        if (optionalPlotIrrigation.isPresent()) {
            return optionalPlotIrrigation.get();
        }
        log.error("Plot Irrigation not found for id {}", plotIrrigationId);
        return null;
    }

    private PlotDTO convertToDTO(Plot plot) {
        PlotDTO plotDTO = modelMapper.map(plot, PlotDTO.class);
        Optional.ofNullable(plot.getSlots()).ifPresent(allSlots ->
                plotDTO.setSlotIds(allSlots.stream().map(Slot::getId).collect(Collectors.toSet()))
        );
        return plotDTO;
    }
}
