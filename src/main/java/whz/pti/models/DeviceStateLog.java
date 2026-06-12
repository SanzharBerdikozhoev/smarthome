package whz.pti.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DeviceStateLog {
    private BigDecimal id;
    private Device device;
    private LocalDateTime time;
    private String state;
    private User user;

    public DeviceStateLog() {}

    public DeviceStateLog(BigDecimal id, Device device, LocalDateTime time, String state, User user) {
        this.id = id;
        this.device = device;
        this.time = time;
        this.state = state;
        this.user = user;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "DeviceStateLog{" +
                "id=" + id +
                ", device=" + device +
                ", time=" + time +
                ", state='" + state + '\'' +
                ", user=" + user +
                '}';
    }
}
