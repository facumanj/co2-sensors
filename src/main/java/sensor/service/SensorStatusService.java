package sensor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sensor.repository.SensorMeasurement;
import sensor.repository.SensorStatus;
import sensor.repository.SensorStatusRepository;
import sensor.utils.StatusValue;

import java.util.Optional;

@Service
public class SensorStatusService {

    @Value("${sensor.measurement.limit}")
    private Integer sensorMeasurementLimit;

    @Value("${sensor.measurement.higher.counter.limit}")
    private Integer sensorMeasurementHigherCounterLimit;

    @Value("${sensor.measurement.lower.counter.limit}")
    private Integer sensorMeasurementLowerCounterLimit;

    @Autowired
    SensorStatusRepository sensorStatusRepository;

    public void processSensorStatus(SensorMeasurement sensorMeasurement) {
        Optional<SensorStatus> optionalSensorStatus = sensorStatusRepository.findById(sensorMeasurement.getUuid());

        SensorStatus sensorStatus = optionalSensorStatus.orElse(new SensorStatus(sensorMeasurement.getUuid(), StatusValue.OK));

        Integer sensorMeasurementValue = sensorMeasurement.getCo2();

        if (sensorMeasurementValue >= sensorMeasurementLimit) {
            processHigherMeasurementValue(sensorStatus);
        } else {
            processLowerMeasurementValue(sensorStatus);
        }

        sensorStatusRepository.save(sensorStatus);
    }

    private void processHigherMeasurementValue(SensorStatus sensorStatus) {
        sensorStatus.incrementHigherCounter();
        sensorStatus.setLowerCounter(0);

        switch(sensorStatus.getStatus()) {
            case OK:
                sensorStatus.setStatus(StatusValue.WARN);
                break;
            case WARN:
                if (sensorStatus.getHigherCounter() >= sensorMeasurementHigherCounterLimit) {
                    sensorStatus.setStatus(StatusValue.ALERT);

                    // TODO: Write alert
                }
                break;
        }
    }

    private void processLowerMeasurementValue(SensorStatus sensorStatus) {
        sensorStatus.incrementLowerCounter();
        sensorStatus.setHigherCounter(0);

        switch (sensorStatus.getStatus()) {
            case WARN:
                sensorStatus.setStatus(StatusValue.OK);
                break;
            case ALERT:
                if (sensorStatus.getLowerCounter() >= sensorMeasurementLowerCounterLimit) {
                    sensorStatus.setStatus(StatusValue.OK);
                }
                break;
        }
    }

    public SensorStatus getStatus(String uuid) {
        return sensorStatusRepository.findById(uuid).orElse(null);
    }
}
