package com.ais.service;

import com.ais.dto.PlotIrrigationDTO;
import com.ais.dto.PlotIrrigationRequestDTO;
import com.ais.exception.PlotIrrigationException;

import java.util.List;

public interface PlotIrrigationService {

    PlotIrrigationDTO configurePlot(String plotId, PlotIrrigationRequestDTO plotIrrigationRequest);

    List<PlotIrrigationDTO> saveAll(List<PlotIrrigationDTO> plotIrrigationDTOList);

    PlotIrrigationDTO getPlotIrrigationByPlotId(String plotId) throws PlotIrrigationException;

    List<PlotIrrigationDTO> findAllPlotIrrigationToCreateSlots(Long currentTime);
}
