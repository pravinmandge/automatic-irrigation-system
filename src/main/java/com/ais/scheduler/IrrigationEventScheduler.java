package com.ais.scheduler;

import com.ais.service.IrrigationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(
        name = "ais.jobs.auto-irrigation.enabled",
        havingValue = "true"
)
public class IrrigationEventScheduler {

    private final IrrigationService irrigationService;

    public IrrigationEventScheduler(IrrigationService irrigationService){
        this.irrigationService = irrigationService;
    }

    @Scheduled(
            fixedDelayString = "${ais.jobs.auto-irrigation.fixed-delay:10000}",
            initialDelayString = "${ais.jobs.auto-irrigation.initial-delay:1000}"
    )
    public void autoIrrigate(){
        log.info("Scheduled 'automatic-irrigation' task has started.");
        irrigationService.irrigate();
        log.info("Scheduled 'automatic-irrigation' task has ended.");
    }
}
