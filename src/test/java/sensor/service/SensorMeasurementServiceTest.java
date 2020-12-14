package sensor.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import sensor.dto.SensorMeasurementDto;
import sensor.repository.SensorMeasurement;
import sensor.repository.SensorMeasurementRepository;

import java.time.OffsetDateTime;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SensorMeasurementServiceTest {
    @Mock
    SensorMeasurementRepository measurementRepository;

    @Mock
    SensorStatusService sensorStatusService;

    @InjectMocks
    SensorMeasurementService sensorMeasurementService;

    @Captor
    ArgumentCaptor<SensorMeasurement> measurementArgumentCaptor;

    private final String uuid = "anyId";
    private final String time = "2019-02-02T18:55:47+00:00";
    private Date dateTime;

    SensorMeasurementDto sensorMeasurementDto;

    @Before
    public void setup() {
        sensorMeasurementDto = new SensorMeasurementDto();

        sensorMeasurementDto.setCo2(123);

        sensorMeasurementDto.setTime(OffsetDateTime.parse(time));

        dateTime = new Date(sensorMeasurementDto.getTime().toInstant().toEpochMilli());
    }

    @Test
    public void saveMeasurement() {
        sensorMeasurementService.saveMeasurement(uuid, sensorMeasurementDto);

        verify(measurementRepository).save(measurementArgumentCaptor.capture());

        SensorMeasurement sensorMeasurement = measurementArgumentCaptor.getValue();

        assertEquals(uuid, sensorMeasurement.getUuid());
        assertEquals(new Integer(123), sensorMeasurement.getCo2());
        assertEquals(dateTime, sensorMeasurement.getTime());

        verify(sensorStatusService).processSensorStatus(sensorMeasurement);
    }

    @Test
    public void getMetrics() {
        sensorMeasurementService.getMetrics(uuid);

        verify(measurementRepository).getMetrics(uuid);
    }
}