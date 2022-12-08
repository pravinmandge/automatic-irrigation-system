package com.ais.sensor.mock;

import com.ais.sensor.model.SensorRequest;
import com.ais.sensor.model.SensorResponse;
import com.ais.sensor.model.SensorStatus;
import com.ais.utils.CommonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockserver.mock.action.ExpectationCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static org.mockserver.model.HttpResponse.response;

@Component
public class SensorExpectationCallback implements ExpectationCallback {

    public HttpResponse handle(HttpRequest httpRequest) {
        try {
            SensorRequest request = CommonUtils.objectMapper().readValue(httpRequest.getBodyAsString(), SensorRequest.class);
            if (request.getId().equals(MockSensorServer.FAILURE_RESPONSE_MOCK_ID)) {
                return response().withStatusCode(500).withBody("Failed to process the request").withDelay(TimeUnit.SECONDS,2);
            } else if (request.getId().equals(MockSensorServer.FAULTY_RESPONSE_MOCK_ID)) {
                return response()
                        .withStatusCode(200).withBody(CommonUtils.objectMapper().writeValueAsString(new SensorResponse(request.getId(), "Sensor is faulty.", SensorStatus.FAULTY))).withDelay(TimeUnit.SECONDS,1);
            } else {
                return response()
                        .withStatusCode(200).withBody(CommonUtils.objectMapper().writeValueAsString(new SensorResponse(request.getId(), "Request received successfully.", SensorStatus.ACTIVE))).withDelay(TimeUnit.SECONDS,1);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
