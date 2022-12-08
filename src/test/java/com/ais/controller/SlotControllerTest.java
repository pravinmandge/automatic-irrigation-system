package com.ais.controller;

import com.ais.dto.SlotDTO;
import com.ais.exception.SlotException;
import com.ais.service.SlotService;
import com.ais.utils.IrrigationError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class SlotControllerTest {

    @Mock
    private SlotService slotService;

    @InjectMocks
    private SlotController slotController;

    @Test
    void testGetAllSlotSuccess() {

        Mockito.when(slotService.getAllSlots()).thenReturn(createSlotDTOList());
        ResponseEntity<List<SlotDTO>> response = slotController.listSlotsByPlotId(null);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetAllSlotWhenNoSlots() {

        Mockito.when(slotService.getAllSlots()).thenReturn(Collections.emptyList());
        try {
            slotController.listSlotsByPlotId(null);
        } catch (SlotException e) {
            Assertions.assertEquals(IrrigationError.SLOT_NOT_FOUND, e.getIrrigationError());
        }
    }

    @Test
    void testGetAllSlotByPlotIdSuccess() {

        Mockito.when(slotService.getAllSlotsByPlotId(any())).thenReturn(createSlotDTOList());
        ResponseEntity<List<SlotDTO>> response = slotController.listSlotsByPlotId("plotId");

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetAllSlotByPlotIdWhenNoSlots() {

        Mockito.when(slotService.getAllSlotsByPlotId(any())).thenReturn(Collections.emptyList());
        try {
            slotController.listSlotsByPlotId("plotId");
        } catch (SlotException e) {
            Assertions.assertEquals(IrrigationError.SLOT_NOT_FOUND, e.getIrrigationError());
        }
    }

    public List<SlotDTO> createSlotDTOList() {
        List<SlotDTO> slotDTOList = new ArrayList<>();
        slotDTOList.add(createSlotDTO());
        slotDTOList.add(createSlotDTO());

        return slotDTOList;
    }

    private SlotDTO createSlotDTO() {
        return SlotDTO.builder().id(UUID.randomUUID().toString())
                .build();
    }
}
