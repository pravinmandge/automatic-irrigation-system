package com.ais.service.impl;

import com.ais.service.AlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AlertServiceImpl implements AlertService {

    @Override
    public void publishAlert(String message) {
        log.info("Publishing alert...: {}", message);
    }
}
