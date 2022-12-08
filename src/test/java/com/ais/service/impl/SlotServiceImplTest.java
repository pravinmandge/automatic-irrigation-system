package com.ais.service.impl;


import com.ais.dto.SlotDTO;
import com.ais.model.Slot;
import com.ais.repository.SlotRepository;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class SlotServiceImplTest {

    @Mock
    private SlotRepository slotRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SlotServiceImpl slotService;

    @Test
    void testGetAllSlotSuccess() {

        Mockito.when(slotRepository.findAll()).thenReturn(createSlotList());
        Mockito.when(modelMapper.map(any(), any())).thenReturn(createSlotDTO(createSlot()));
        List<SlotDTO> slotDTOList = slotService.getAllSlots();

        Assertions.assertNotNull(slotDTOList);
        Assertions.assertEquals(2, slotDTOList.size());
    }

    @Test
    void testGetAllSlotWhenNoSlots() {

        Mockito.when(slotRepository.findAll()).thenReturn(Collections.emptyList());
        List<SlotDTO> slotDTOList = slotService.getAllSlots();

        Assertions.assertNotNull(slotDTOList);
        Assertions.assertEquals(0, slotDTOList.size());
    }

    @Test
    void testGetAllSlotByPlotIdSuccess() {

        Mockito.when(slotRepository.findAllByPlot_Id(any())).thenReturn(createSlotList());
        List<SlotDTO> slotDTOList = slotService.getAllSlotsByPlotId("plotId");

        Assertions.assertNotNull(slotDTOList);
        Assertions.assertEquals(2, slotDTOList.size());
    }

    @Test
    void testGetAllSlotByPlotIdWhenNoSlots() {

        Mockito.when(slotRepository.findAllByPlot_Id(any())).thenReturn(Collections.emptyList());

        List<SlotDTO> slotDTOList = slotService.getAllSlotsByPlotId("plotId");

        Assertions.assertNotNull(slotDTOList);
        Assertions.assertEquals(0, slotDTOList.size());
    }

    public List<Slot> createSlotList(){
        List<Slot> slotList = new ArrayList<>();
        slotList.add(createSlot());
        slotList.add(createSlot());
        return slotList;
    }

    private Slot createSlot() {
        return Slot.builder().id(UUID.randomUUID().toString())
                .build();
    }

    private SlotDTO createSlotDTO(Slot slot) {
        return SlotDTO.builder().id(slot.getId())
                .build();
    }
}
