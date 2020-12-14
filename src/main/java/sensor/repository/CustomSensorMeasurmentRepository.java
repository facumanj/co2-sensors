package sensor.repository;

import java.util.List;

public interface CustomSensorMeasurmentRepository {
    SensorMetrics getMetrics(String uuid);
}
