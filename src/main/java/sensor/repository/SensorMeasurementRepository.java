package sensor.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorMeasurementRepository extends MongoRepository<SensorMeasurement, String>, CustomSensorMeasurmentRepository {
}
