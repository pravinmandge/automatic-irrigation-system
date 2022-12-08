package com.ais.sensor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorResponse {

    private String id;
    private String message;
    private SensorStatus status;
}
