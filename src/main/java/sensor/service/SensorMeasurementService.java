package sensor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sensor.dto.SensorMeasurementDto;
import sensor.repository.SensorMeasurement;
import sensor.repository.SensorMeasurementRepository;
import sensor.repository.SensorMetrics;
import sensor.repository.SensorStatus;

import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@Service
public class SensorMeasurementService {

    @Autowired
    SensorMeasurementRepository measurementRepository;

    @Autowired
    SensorStatusService sensorStatusService;

    public void saveMeasurement(String uuid, SensorMeasurementDto sensorMeasurementDto) {
        SensorMeasurement measurement = new SensorMeasurement();

        measurement.setUuid(uuid);
        measurement.setCo2(sensorMeasurementDto.getCo2());
        measurement.setTime(new Date(sensorMeasurementDto.getTime().toInstant().toEpochMilli()));

        measurementRepository.save(measurement);

        // Since measurentments are received one per minute, no need to handle transactions here
        sensorStatusService.processSensorStatus(measurement);
    }

    public SensorMetrics getMetrics(String uuid) {
        return measurementRepository.getMetrics(uuid);
    }
}
