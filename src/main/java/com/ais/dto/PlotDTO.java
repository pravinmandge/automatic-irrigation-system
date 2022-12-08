package com.ais.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlotDTO {
    private String id;
    @NotBlank
    private String name;
    private Set<String> slotIds = new LinkedHashSet<>();
    private String sensorUrl;
    private String plotIrrigationId;
}
