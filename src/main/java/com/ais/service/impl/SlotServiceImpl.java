package com.ais.service.impl;

import com.ais.dto.SlotDTO;
import com.ais.exception.PlotException;
import com.ais.exception.SlotException;
import com.ais.model.Plot;
import com.ais.model.Slot;
import com.ais.model.SlotStatus;
import com.ais.repository.PlotRepository;
import com.ais.repository.SlotRepository;
import com.ais.service.SlotService;
import com.ais.utils.IrrigationError;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SlotServiceImpl implements SlotService {

    private final SlotRepository slotRepository;
    private final PlotRepository plotRepository;
    private final ModelMapper modelMapper;

    public SlotServiceImpl(SlotRepository slotRepository, PlotRepository plotRepository, ModelMapper modelMapper) {
        this.slotRepository = slotRepository;
        this.plotRepository = plotRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<SlotDTO> addAllSlots(List<SlotDTO> slotDTOList) {
        List<Slot> slotList = slotDTOList.stream().map(slotDTO -> Slot.builder()
                .id(UUID.randomUUID().toString())
                .waterNeeded(slotDTO.getWaterNeeded())
                .status(SlotStatus.PENDING)
                .lastRetryTime(slotDTO.getLastRetryTime())
                .retryCount(slotDTO.getRetryCount())
                .durationInMin(slotDTO.getDurationInMin())
                .intervalInMin(slotDTO.getIntervalInMin())
                .plot(fetchPlotById(slotDTO.getPlotId()))
                .build()).collect(Collectors.toList());

        slotRepository.saveAll(slotList);
        return slotList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public SlotDTO updateSlot(String id, SlotDTO slotDTO) {
        Optional<Slot> optionalSlot = slotRepository.findById(id);
        if (optionalSlot.isPresent()) {
            Slot slot = optionalSlot.get();
            slot.setWaterNeeded(slotDTO.getWaterNeeded());
            slot.setDurationInMin(slotDTO.getDurationInMin());
            slot.setRetryCount(slotDTO.getRetryCount());
            slot.setIntervalInMin(slotDTO.getIntervalInMin());
            slot.setLastRetryTime(slotDTO.getLastRetryTime());
            slot.setPlot(fetchPlotById(slotDTO.getPlotId()));
            slot.setStatus(slotDTO.getStatus());
            slotRepository.save(slot);
            return convertToDTO(slot);
        } else {
            log.error("Slot not found for id {}", id);
            throw new SlotException(IrrigationError.SLOT_NOT_FOUND);
        }
    }

    @Override
    public List<SlotDTO> getAllSlots() {
        return slotRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<SlotDTO> getAllSlotsByPlotId(String plotId) {
        return slotRepository.findAllByPlot_Id(plotId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private SlotDTO convertToDTO(Slot slot) {
        return modelMapper.map(slot, SlotDTO.class);
    }

    private Plot fetchPlotById(String id) {
        Optional<Plot> plot = plotRepository.findById(id);
        if (plot.isPresent()) {
            return plot.get();
        } else {
            log.error("Plot not found for id {}", id);
            throw new PlotException(IrrigationError.PLOT_NOT_FOUND);
        }
    }
}
