package com.ais.scheduler;

import com.ais.service.IrrigationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(
        name = "ais.jobs.slot-schedule.enabled",
        havingValue = "true"
)
public class SlotEventScheduler {

    private final IrrigationService irrigationService;

    public SlotEventScheduler(IrrigationService irrigationService){
        this.irrigationService = irrigationService;
    }

    @Scheduled(
            fixedDelayString = "${ais.jobs.slot-schedule.fixed-delay:10000}",
            initialDelayString = "${ais.jobs.slot-schedule.initial-delay:1000}"
    )
    public void autoIrrigate(){
        log.info("Scheduled 'slot-schedule' task has started.");
        irrigationService.createSlotsForValidIrrigation();
        log.info("Scheduled 'slot-schedule' task has ended.");
    }
}
