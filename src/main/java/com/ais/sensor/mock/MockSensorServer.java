package com.ais.sensor.mock;

import lombok.extern.slf4j.Slf4j;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.unlimited;
import static org.mockserver.model.HttpClassCallback.callback;
import static org.mockserver.model.HttpRequest.request;

@Component
@Slf4j
public class MockSensorServer {

    public static final String FAILURE_RESPONSE_MOCK_ID = "FAILURE_RESPONSE_MOCK_ID";
    public static final String FAULTY_RESPONSE_MOCK_ID = "FAULTY_RESPONSE_MOCK_ID";

    private static ClientAndServer mockServer;

    @PostConstruct
    public void start(){
        mockServer = startClientAndServer(1080);

        new MockServerClient("127.0.0.1", 1080)
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/api/v1/sensors")
                                .withHeader("\"Content-type\", \"application/json\""),
                        unlimited())
                .callback(
                        callback()
                                .withCallbackClass("com.ais.sensor.mock.SensorExpectationCallback")
                );
    }

    @PreDestroy
    public void stop(){
        mockServer.stop();
    }

}
