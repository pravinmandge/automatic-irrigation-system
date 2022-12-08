package com.ais.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlotIrrigationRequestDTO {

    private Integer durationInMin;
    private Integer intervalInMin;
    private Integer waterNeededInMm;
}
