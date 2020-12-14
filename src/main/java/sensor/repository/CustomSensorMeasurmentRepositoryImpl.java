package sensor.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

public class CustomSensorMeasurmentRepositoryImpl implements CustomSensorMeasurmentRepository {
    @Autowired
    MongoTemplate mongoTemplate;

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public SensorMetrics getMetrics(String uuid) {
        Date nowMinus30Days = toDate(LocalDateTime.now().minusDays(30));

        MatchOperation matches = match(Criteria.where("uuid").is(uuid).and("time").gte(nowMinus30Days));

        GroupOperation groupByNothing = group()
                .max("co2").as("maxLast30Days")
                .avg("co2").as("avgLast30Days");

        AggregationResults<SensorMetrics> result = mongoTemplate.aggregate(
                newAggregation(matches, groupByNothing), "sensorMeasurement", SensorMetrics.class);

        return result.getUniqueMappedResult();
    }
}
