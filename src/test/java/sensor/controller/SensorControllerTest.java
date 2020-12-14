package sensor.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import sensor.dto.SensorMeasurementDto;
import sensor.repository.SensorMetrics;
import sensor.repository.SensorStatus;
import sensor.service.SensorMeasurementService;
import sensor.service.SensorStatusService;
import sensor.utils.StatusValue;

import java.time.OffsetDateTime;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SensorController.class)
public class SensorControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    SensorMeasurementService sensorMeasurementService;

    @MockBean
    SensorStatusService sensorStatusService;

    @Captor
    ArgumentCaptor<String> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<SensorMeasurementDto> measurementDtoArgumentCaptor;

    private final String mappingPrefix = "/api/v1/sensors";

    private final String uuid = "1";

    @Test
    public void saveMeasurements() throws Exception {
        final String jsonContent = "{\n" +
                "\"co2\" : 2000,\n" +
                "\"time\" : \"2019-02-01T18:55:47+00:00\" }";

        mvc.perform(post(mappingPrefix + "/"+uuid+"/measurements")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonContent))
        .andExpect(status().isOk());

        verify(sensorMeasurementService).saveMeasurement(uuidArgumentCaptor.capture(), measurementDtoArgumentCaptor.capture());

        assertEquals(uuid, uuidArgumentCaptor.getValue());

        SensorMeasurementDto measurementDto = measurementDtoArgumentCaptor.getValue();

        assertEquals(new Integer(2000), measurementDto.getCo2());
        assertEquals(OffsetDateTime.parse("2019-02-01T18:55:47+00:00"), measurementDto.getTime());
    }

    @Test
    public void getStatus() throws Exception {
        SensorStatus expectedStatus = new SensorStatus(uuid, StatusValue.OK);

        when(sensorStatusService.getStatus(uuid)).thenReturn(expectedStatus);

        MvcResult result = mvc.perform(get(mappingPrefix + "/"+uuid))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("{\"status\":\"OK\"}", result.getResponse().getContentAsString());
    }

    @Test
    public void getStatusNotFound() throws Exception {
        mvc.perform(get(mappingPrefix + "/"+uuid))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getMetrics() throws Exception {
        SensorMetrics expectedMetrics = new SensorMetrics();

        expectedMetrics.setAvgLast30Days(123);
        expectedMetrics.setMaxLast30Days(456);

        when(sensorMeasurementService.getMetrics(uuid)).thenReturn(expectedMetrics);

        MvcResult result = mvc.perform(get(mappingPrefix + "/" + uuid + "/metrics"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("{\"maxLast30Days\":456,\"avgLast30Days\":123}", result.getResponse().getContentAsString());
    }

    @Test
    public void getMetricsNotFound() throws Exception {
        mvc.perform(get(mappingPrefix + "/" + uuid + "/metrics"))
                .andExpect(status().isNotFound());
    }
}