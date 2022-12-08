package com.ais.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class AlertServiceImplTest {

    @InjectMocks
    private AlertServiceImpl alertService;

    @Test
    void testGetASuccess() {
        alertService.publishAlert("Irrigation failed: Sensor fault");
    }
}
