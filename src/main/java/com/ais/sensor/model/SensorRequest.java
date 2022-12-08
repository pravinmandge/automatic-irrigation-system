package com.ais.sensor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorRequest {

    private String id;
    private Integer durationInMin;
    private Integer waterNeeded;
}
