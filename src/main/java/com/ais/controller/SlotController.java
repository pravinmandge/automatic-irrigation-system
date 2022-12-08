package com.ais.controller;

import com.ais.dto.SlotDTO;
import com.ais.exception.SlotException;
import com.ais.service.SlotService;
import com.ais.utils.IrrigationError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SlotController {

    private static final String API_BASE_URL = "/api/v1";
    public static final String SLOTS_URL = API_BASE_URL + "/slots";

    private SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @Operation(summary = "Get all the slots optionally by plot id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Found all the slots",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = SlotDTO.class)))
                            })
            })
    @GetMapping(path = SLOTS_URL, produces = "application/json")
    ResponseEntity<List<SlotDTO>> listSlotsByPlotId(@RequestParam(name = "plotId", required = false) String plotId) {
        List<SlotDTO> slots = null;
        if (StringUtils.hasLength(plotId)) {
            slots = slotService.getAllSlotsByPlotId(plotId);
        } else {
            slots = slotService.getAllSlots();
        }
        if (!slots.isEmpty()) {
            return ResponseEntity.ok(slots);
        }
        throw new SlotException(IrrigationError.SLOT_NOT_FOUND);
    }
}
