package com.ais.service.impl;

import com.ais.dto.PlotDTO;
import com.ais.dto.PlotIrrigationDTO;
import com.ais.dto.SlotDTO;
import com.ais.model.IrrigationStatus;
import com.ais.model.SlotStatus;
import com.ais.sensor.SensorIntegrationService;
import com.ais.sensor.model.SensorResponse;
import com.ais.sensor.model.SensorStatus;
import com.ais.service.AlertService;
import com.ais.service.IrrigationService;
import com.ais.service.PlotIrrigationService;
import com.ais.service.PlotService;
import com.ais.service.SlotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class IrrigationServiceImpl implements IrrigationService {

    private final PlotService plotService;
    private final SlotService slotService;
    private final PlotIrrigationService plotIrrigationService;
    private final AlertService alertService;
    private final SensorIntegrationService sensorIntegrationService;

    @Value("${ais.jobs.auto-irrigation.retry.count:3}")
    private Integer sensorRetryCount;
    @Value("${ais.jobs.auto-irrigation.retry.intervalInMillis:10000}")
    private Integer sensorRetryInterval;

    public IrrigationServiceImpl(PlotService plotService, SlotService slotService, PlotIrrigationService plotIrrigationService, AlertService alertService, SensorIntegrationService sensorIntegrationService) {
        this.plotService = plotService;
        this.slotService = slotService;
        this.plotIrrigationService = plotIrrigationService;
        this.alertService = alertService;
        this.sensorIntegrationService = sensorIntegrationService;
    }

    @Override
    @Transactional
    public void irrigate() {
        log.info("Starting automatic irrigation");
        try {
            irrigateAllPendingSlots();
        } catch (Exception e) {
            log.error("Irrigation processing failed due to..", e);
        }
        log.info("Completed automatic irrigation");
    }

    private void irrigateAllPendingSlots() {
        List<SlotDTO> allSlots = slotService.getAllSlots();
        List<PlotIrrigationDTO> allPlotIrrigation = new ArrayList<>();
        allSlots.forEach(slot -> {
            // for slots with PENDING status
            if (slot.getStatus() == SlotStatus.PENDING) {
                // proceed only if trying after the mentioned retry interval
                long interval = System.currentTimeMillis() - slot.getLastRetryTime();
                if (interval >= sensorRetryInterval) {
                    PlotDTO plotDTO = plotService.getPlot(slot.getPlotId());
                    SensorResponse sensorResponse = sensorIntegrationService.irrigate(plotDTO.getSensorUrl(), slot);

                    slot.setLastRetryTime(System.currentTimeMillis());

                    if (sensorResponse.getStatus() == SensorStatus.ACTIVE) {
                        slot.setStatus(SlotStatus.IN_PROGRESS);
                    } else {
                        int retryCount = slot.getRetryCount();
                        retryCount++;
                        slot.setRetryCount(retryCount);

                        if (slot.getRetryCount() >= sensorRetryCount) {
                            slot.setStatus(SlotStatus.FAILED);

                            PlotIrrigationDTO plotIrrigationDTO = plotIrrigationService.getPlotIrrigationByPlotId(slot.getPlotId());
                            calculateAndSetNextIrrigationTime(plotIrrigationDTO);
                            allPlotIrrigation.add(plotIrrigationDTO);
                            // publish alert
                            alertService.publishAlert("MAX attempt exceeded. Sensor status is " + sensorResponse.getStatus() + ". Please check the sensor!");
                        }
                    }
                }
                slotService.updateSlot(slot.getId(), slot);
            } else if (slot.getStatus() == SlotStatus.IN_PROGRESS && System.currentTimeMillis() >= (slot.getLastRetryTime() + (slot.getDurationInMin() * 60 * 1000))) {
                slot.setStatus(SlotStatus.COMPLETED);

                PlotIrrigationDTO plotIrrigationDTO = plotIrrigationService.getPlotIrrigationByPlotId(slot.getPlotId());
                calculateAndSetNextIrrigationTime(plotIrrigationDTO);
                allPlotIrrigation.add(plotIrrigationDTO);

                slotService.updateSlot(slot.getId(), slot);
            }
        });

        plotIrrigationService.saveAll(allPlotIrrigation);
    }

    @Override
    public void createSlotsForValidIrrigation() {
        // find all valid plot irrigation for slots creation
        List<PlotIrrigationDTO> plotIrrigationList = plotIrrigationService.findAllPlotIrrigationToCreateSlots(System.currentTimeMillis());
        // create slots
        List<SlotDTO> slotDTOList = plotIrrigationList.stream().map(plotIrrigation -> {
            SlotDTO slotDTO = new SlotDTO();
            slotDTO.setPlotId(plotIrrigation.getPlotId());
            slotDTO.setDurationInMin(plotIrrigation.getDurationInMin());
            slotDTO.setRetryCount(0);
            slotDTO.setLastRetryTime(System.currentTimeMillis());
            slotDTO.setIntervalInMin(plotIrrigation.getIntervalInMin());
            slotDTO.setWaterNeeded(plotIrrigation.getWaterNeededInMm());
            slotDTO.setStatus(SlotStatus.PENDING);
            return slotDTO;
        }).collect(Collectors.toList());
        // save slots
        slotService.addAllSlots(slotDTOList);
        // update plot irrigation status
        plotIrrigationList.forEach(plotIrrigation -> {
            plotIrrigation.setStatus(IrrigationStatus.IN_PROGRESS);
            plotIrrigation.setUpdatedTime(System.currentTimeMillis());
        });

        plotIrrigationService.saveAll(plotIrrigationList);
    }

    private void calculateAndSetNextIrrigationTime(PlotIrrigationDTO plotIrrigationDTO) {
        Long nextIrrigationTime = System.currentTimeMillis() + (plotIrrigationDTO.getIntervalInMin() * 60 * 1000);
        plotIrrigationDTO.setNextIrrigationTime(nextIrrigationTime);
        plotIrrigationDTO.setStatus(IrrigationStatus.IDLE);
        plotIrrigationDTO.setUpdatedTime(System.currentTimeMillis());
    }
}
