package com.ais.controller;

import com.ais.dto.PlotDTO;
import com.ais.dto.PlotIrrigationDTO;
import com.ais.dto.PlotIrrigationRequestDTO;
import com.ais.exception.PlotException;
import com.ais.service.PlotIrrigationService;
import com.ais.service.PlotService;
import com.ais.utils.IrrigationError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
public class PlotController {

    private static final String API_BASE_URL = "/api/v1";
    public static final String PLOTS_URL = API_BASE_URL+ "/plots";
    public static final String PLOT_BY_ID_URL = API_BASE_URL+ "/plots/{id}";
    public static final String CONFIGURE_PLOT_URL = API_BASE_URL+ "/plots/{id}/configure";

    private final PlotService plotService;
    private final PlotIrrigationService plotIrrigationService;

    public PlotController(PlotService plotService, PlotIrrigationService plotIrrigationService) {
        this.plotService = plotService;
        this.plotIrrigationService = plotIrrigationService;
    }

    @Operation(summary = "Get all the plots")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Found all the plots",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = PlotDTO.class)))
                            })
            })
    @GetMapping(path = PLOTS_URL, produces = "application/json")
    ResponseEntity<List<PlotDTO>> getAllPlots() {
        List<PlotDTO> plots = plotService.getAllPlots();
        if(plots != null){
            return ResponseEntity.ok(plots);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Operation(summary = "Get a plot by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the plot",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PlotDTO.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Plot not found")})
    @GetMapping(path = PLOT_BY_ID_URL, produces = "application/json")
    ResponseEntity<PlotDTO> getPlot(@PathVariable(name = "id") @NotBlank String id) throws PlotException {
        PlotDTO plotDTO = plotService.getPlot(id);
        if(plotDTO != null){
            return ResponseEntity.ok(plotDTO);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Create a plot")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Created a plot",
            content= {
            @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PlotDTO.class))
    })})
    @PostMapping(path = PLOTS_URL, consumes = "application/json", produces = "application/json")
    ResponseEntity createPlot(@Valid @RequestBody PlotDTO plotDTO) {
        PlotDTO plot = plotService.addPlot(plotDTO);
        if(plot != null){
           return new ResponseEntity<>(plot, HttpStatus.CREATED);
        }
        throw new PlotException(IrrigationError.CREATE_PLOT_FAILED);
    }

    @Operation(summary = "Configure a plot for irrigation by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configure plot successful",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PlotIrrigationDTO.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Plot not found", content = @Content)})
    @PutMapping(path = CONFIGURE_PLOT_URL, consumes = "application/json", produces = "application/json")
    ResponseEntity<PlotIrrigationDTO> configurePlot(@PathVariable(name = "id") @NotBlank String id, @Valid @RequestBody PlotIrrigationRequestDTO plotIrrigationRequestDTO) {
        PlotIrrigationDTO plotIrrigationDTO = plotIrrigationService.configurePlot(id, plotIrrigationRequestDTO);
        return ResponseEntity.ok().body(plotIrrigationDTO);
    }

    @Operation(summary = "Update a plot by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a plot successful",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PlotDTO.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Plot not found", content = @Content)})
    @PutMapping(path = PLOT_BY_ID_URL, consumes = "application/json", produces = "application/json")
    ResponseEntity<PlotDTO> updatePlot(@PathVariable(name = "id") @NotBlank String id, @Valid @RequestBody PlotDTO plotDTO) {
        PlotDTO plot = plotService.updatePlot(id, plotDTO);
        return ResponseEntity.ok().body(plot);
    }
}
