package com.ais.sensor.impl;

import com.ais.dto.SlotDTO;
import com.ais.sensor.SensorIntegrationService;
import com.ais.sensor.mock.MockSensorServer;
import com.ais.sensor.model.SensorRequest;
import com.ais.sensor.model.SensorResponse;
import com.ais.sensor.model.SensorStatus;
import com.ais.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockserver.model.HttpResponse.response;

@Service
@Slf4j
public class SensorIntegrationServiceImpl implements SensorIntegrationService {

    @Override
    public SensorResponse irrigate(String sensorUrl, SlotDTO slotDTO) {
        SensorRequest sensorRequest = SensorRequest.builder()
                .id(UUID.randomUUID().toString())
                .durationInMin(slotDTO.getDurationInMin())
                .waterNeeded(slotDTO.getWaterNeeded())
                .build();

        if(sensorUrl.contains("fail2") && slotDTO.getRetryCount() <= 2) {
            return new SensorResponse(sensorRequest.getId(), "Sensor is faulty.", SensorStatus.FAULTY);
        }

        return sendToSensor(sensorUrl, sensorRequest);
    }

    private SensorResponse sendToSensor(String url, SensorRequest sensorRequest) {
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);

            String json = CommonUtils.objectMapper().writeValueAsString(sensorRequest);
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);

            CloseableHttpResponse response = client.execute(httpPost);
            if(response.getCode() == 200) {
                return CommonUtils.objectMapper().readValue(EntityUtils.toString(response.getEntity()), SensorResponse.class);
            }
        } catch (Exception e){
            log.error("Error while sending request to sensor", e);
        }
        return null;
    }
}
