package sensor.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorStatusRepository extends MongoRepository<SensorStatus, String> {
}
