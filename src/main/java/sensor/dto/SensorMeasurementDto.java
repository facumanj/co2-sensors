package sensor.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

public class SensorMeasurementDto {
    private Integer co2;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime time;

    public Integer getCo2() {
        return co2;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    public void setCo2(Integer co2) {
        this.co2 = co2;
    }

    public void setTime(OffsetDateTime time) {
        this.time = time;
    }
}
