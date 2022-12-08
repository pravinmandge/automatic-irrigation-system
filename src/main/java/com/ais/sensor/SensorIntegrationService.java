package com.ais.sensor;

import com.ais.dto.SlotDTO;
import com.ais.sensor.model.SensorResponse;

public interface SensorIntegrationService {

    public SensorResponse irrigate(String sensorUrl, SlotDTO slotDTO);
}
