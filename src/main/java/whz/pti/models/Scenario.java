package whz.pti.models;

import java.math.BigDecimal;
import java.time.LocalTime;

public class Scenario {
    private BigDecimal id;
    private LocalTime startTime;
    private LocalTime endTime;

    private Scenario() {}

    public Scenario(BigDecimal id, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }


    @Override
    public String toString() {
        return "Scenario{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
