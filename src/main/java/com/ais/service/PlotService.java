package com.ais.service;


import com.ais.dto.PlotDTO;
import com.ais.exception.PlotException;

import java.util.List;

public interface PlotService {

    PlotDTO addPlot(PlotDTO plotDTO);

    PlotDTO updatePlot(String id, PlotDTO plotDTO) throws PlotException;

    PlotDTO getPlot(String id) throws PlotException;

    List<PlotDTO> getAllPlots();
}
