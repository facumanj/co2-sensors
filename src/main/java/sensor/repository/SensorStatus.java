package sensor.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import sensor.utils.StatusValue;

public class SensorStatus {
    @Id
    @JsonIgnore
    private String uuid;

    private StatusValue status;

    @JsonIgnore
    private int higherCounter;

    @JsonIgnore
    private int lowerCounter;

    public SensorStatus(String uuid, StatusValue status) {
        this.uuid = uuid;
        this.status = status;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public StatusValue getStatus() {
        return status;
    }

    public void setStatus(StatusValue status) {
        this.status = status;
    }

    public Integer getHigherCounter() {
        return higherCounter;
    }

    public void setHigherCounter(int higherCounter) {
        this.higherCounter = higherCounter;
    }

    public Integer getLowerCounter() {
        return lowerCounter;
    }

    public void setLowerCounter(int lowerCounter) {
        this.lowerCounter = lowerCounter;
    }

    public void incrementHigherCounter() {
        if (higherCounter<Integer.MAX_VALUE) {
            higherCounter++;
        }
    }

    public void incrementLowerCounter() {
        if (lowerCounter<Integer.MAX_VALUE) {
            lowerCounter++;
        }
    }
}
