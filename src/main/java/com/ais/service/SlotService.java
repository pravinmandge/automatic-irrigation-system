package com.ais.service;

import com.ais.dto.SlotDTO;

import java.util.List;

public interface SlotService {

    List<SlotDTO> addAllSlots(List<SlotDTO> slotDTOList);

    SlotDTO updateSlot(String id, SlotDTO slotDTO);

    List<SlotDTO> getAllSlots();

    List<SlotDTO> getAllSlotsByPlotId(String plotId);

}
