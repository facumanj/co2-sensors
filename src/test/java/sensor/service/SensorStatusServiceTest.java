package sensor.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import sensor.repository.SensorMeasurement;
import sensor.repository.SensorStatus;
import sensor.repository.SensorStatusRepository;
import sensor.utils.StatusValue;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SensorStatusServiceTest {

    @Mock
    SensorStatusRepository sensorStatusRepository;

    @InjectMocks
    SensorStatusService sensorStatusService;

    private final String uuid = "anyId";
    
    private SensorMeasurement lowerMeasurement;
    private SensorMeasurement higherMeasurement;

    @Captor
    ArgumentCaptor<SensorStatus> sensorStatusArgumentCaptor;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(sensorStatusService, "sensorMeasurementLimit", 2000);
        ReflectionTestUtils.setField(sensorStatusService, "sensorMeasurementHigherCounterLimit", 3);
        ReflectionTestUtils.setField(sensorStatusService, "sensorMeasurementLowerCounterLimit", 3);
        
        lowerMeasurement = new SensorMeasurement();
        lowerMeasurement.setUuid(uuid);
        lowerMeasurement.setCo2(1000);

        higherMeasurement = new SensorMeasurement();
        higherMeasurement.setUuid(uuid);
        higherMeasurement.setCo2(2000);
    }

    @Test
    public void processNewLower() {
        when(sensorStatusRepository.findById(uuid)).thenReturn(Optional.empty());

        sensorStatusService.processSensorStatus(lowerMeasurement);

        verify(sensorStatusRepository).save(sensorStatusArgumentCaptor.capture());

        SensorStatus result = sensorStatusArgumentCaptor.getValue();

        assertEquals(uuid, result.getUuid());
        assertEquals(StatusValue.OK, result.getStatus());
        assertEquals(1, result.getLowerCounter());
        assertEquals(0, result.getHigherCounter());
    }

    @Test
    public void processLowerFromOk() {
        SensorStatus actual = new SensorStatus(uuid, StatusValue.OK);
        actual.setLowerCounter(1);

        when(sensorStatusRepository.findById(uuid)).thenReturn(Optional.of(actual));

        sensorStatusService.processSensorStatus(lowerMeasurement);

        verify(sensorStatusRepository).save(sensorStatusArgumentCaptor.capture());

        SensorStatus result = sensorStatusArgumentCaptor.getValue();

        assertEquals(uuid, result.getUuid());
        assertEquals(StatusValue.OK, result.getStatus());
        assertEquals(2, result.getLowerCounter());
        assertEquals(0, result.getHigherCounter());
    }

    @Test
    public void processLowerFromWarn() {
        SensorStatus actual = new SensorStatus(uuid, StatusValue.WARN);
        actual.setHigherCounter(1);

        when(sensorStatusRepository.findById(uuid)).thenReturn(Optional.of(actual));

        sensorStatusService.processSensorStatus(lowerMeasurement);

        verify(sensorStatusRepository).save(sensorStatusArgumentCaptor.capture());

        SensorStatus result = sensorStatusArgumentCaptor.getValue();

        assertEquals(uuid, result.getUuid());
        assertEquals(StatusValue.OK, result.getStatus());
        assertEquals(1, result.getLowerCounter());
        assertEquals(0, result.getHigherCounter());
    }

    @Test
    public void processLowerLessThanCounterFromAlert() {
        SensorStatus actual = new SensorStatus(uuid, StatusValue.ALERT);
        actual.setHigherCounter(3);

        when(sensorStatusRepository.findById(uuid)).thenReturn(Optional.of(actual));

        sensorStatusService.processSensorStatus(lowerMeasurement);

        verify(sensorStatusRepository).save(sensorStatusArgumentCaptor.capture());

        SensorStatus result = sensorStatusArgumentCaptor.getValue();

        assertEquals(uuid, result.getUuid());
        assertEquals(StatusValue.ALERT, result.getStatus());
        assertEquals(1, result.getLowerCounter());
        assertEquals(0, result.getHigherCounter());
    }

    @Test
    public void processLowerGreaterThanCounterFromAlert() {
        SensorStatus actual = new SensorStatus(uuid, StatusValue.ALERT);
        actual.setHigherCounter(3);
        actual.setLowerCounter(2);

        when(sensorStatusRepository.findById(uuid)).thenReturn(Optional.of(actual));

        sensorStatusService.processSensorStatus(lowerMeasurement);

        verify(sensorStatusRepository).save(sensorStatusArgumentCaptor.capture());

        SensorStatus result = sensorStatusArgumentCaptor.getValue();

        assertEquals(uuid, result.getUuid());
        assertEquals(StatusValue.OK, result.getStatus());
        assertEquals(3, result.getLowerCounter());
        assertEquals(0, result.getHigherCounter());
    }

    @Test
    public void processNewHigher() {
        when(sensorStatusRepository.findById(uuid)).thenReturn(Optional.empty());

        sensorStatusService.processSensorStatus(higherMeasurement);

        verify(sensorStatusRepository).save(sensorStatusArgumentCaptor.capture());

        SensorStatus result = sensorStatusArgumentCaptor.getValue();

        assertEquals(uuid, result.getUuid());
        assertEquals(StatusValue.WARN, result.getStatus());
        assertEquals(0, result.getLowerCounter());
        assertEquals(1, result.getHigherCounter());
    }

    @Test
    public void processHigherLessThanCounterFromWarn() {
        SensorStatus actual = new SensorStatus(uuid, StatusValue.WARN);
        actual.setHigherCounter(1);

        when(sensorStatusRepository.findById(uuid)).thenReturn(Optional.of(actual));

        sensorStatusService.processSensorStatus(higherMeasurement);

        verify(sensorStatusRepository).save(sensorStatusArgumentCaptor.capture());

        SensorStatus result = sensorStatusArgumentCaptor.getValue();

        assertEquals(uuid, result.getUuid());
        assertEquals(StatusValue.WARN, result.getStatus());
        assertEquals(0, result.getLowerCounter());
        assertEquals(2, result.getHigherCounter());
    }

    @Test
    public void processHigherGreaterThanCounterFromWarn() {
        SensorStatus actual = new SensorStatus(uuid, StatusValue.WARN);
        actual.setHigherCounter(2);

        when(sensorStatusRepository.findById(uuid)).thenReturn(Optional.of(actual));

        sensorStatusService.processSensorStatus(higherMeasurement);

        verify(sensorStatusRepository).save(sensorStatusArgumentCaptor.capture());

        SensorStatus result = sensorStatusArgumentCaptor.getValue();

        assertEquals(uuid, result.getUuid());
        assertEquals(StatusValue.ALERT, result.getStatus());
        assertEquals(0, result.getLowerCounter());
        assertEquals(3, result.getHigherCounter());
    }

    @Test
    public void processHigherFromAlert() {
        SensorStatus actual = new SensorStatus(uuid, StatusValue.ALERT);
        actual.setLowerCounter(1);
        actual.setHigherCounter(0);

        when(sensorStatusRepository.findById(uuid)).thenReturn(Optional.of(actual));

        sensorStatusService.processSensorStatus(higherMeasurement);

        verify(sensorStatusRepository).save(sensorStatusArgumentCaptor.capture());

        SensorStatus result = sensorStatusArgumentCaptor.getValue();

        assertEquals(uuid, result.getUuid());
        assertEquals(StatusValue.ALERT, result.getStatus());
        assertEquals(0, result.getLowerCounter());
        assertEquals(1, result.getHigherCounter());
    }

    @Test
    public void getStatusTrue() {
        SensorStatus sensorStatusExpected = new SensorStatus(uuid, StatusValue.OK);
        Optional<SensorStatus> optionalExpected = Optional.of(sensorStatusExpected);
        
        when(sensorStatusRepository.findById(uuid)).thenReturn(optionalExpected);
        
        SensorStatus result = sensorStatusService.getStatus(uuid);
        
        assertEquals(sensorStatusExpected, result);
    }
    
    @Test
    public void getStatusFalse() {
        Optional<SensorStatus> optionalExpected = Optional.empty();

        when(sensorStatusRepository.findById(uuid)).thenReturn(optionalExpected);

        SensorStatus result = sensorStatusService.getStatus(uuid);

        assertNull(result);
    }
}