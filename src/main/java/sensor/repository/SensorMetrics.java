package sensor.repository;

public class SensorMetrics {
    private Integer maxLast30Days;

    private Integer avgLast30Days;

    public Integer getMaxLast30Days() {
        return maxLast30Days;
    }

    public void setMaxLast30Days(Integer maxLast30Days) {
        this.maxLast30Days = maxLast30Days;
    }

    public Integer getAvgLast30Days() {
        return avgLast30Days;
    }

    public void setAvgLast30Days(Integer avgLast30Days) {
        this.avgLast30Days = avgLast30Days;
    }
}
