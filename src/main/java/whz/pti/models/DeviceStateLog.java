package whz.pti.models;

import whz.pti.utils.annotations.Column;

import java.time.LocalDateTime;

public class DeviceStateLog {
    private Long id;
    @Column(name = "device_id")
    private Long  device;
    @Column(name = "timestamp")
    private LocalDateTime time;
    @Column(name = "state_value")
    private String state;
    @Column(name = "user_id")
    private Long user;

    public DeviceStateLog() {}

    public DeviceStateLog(Long id, Long device, LocalDateTime time, String state, Long  user) {
        this.id = id;
        this.device = device;
        this.time = time;
        this.state = state;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDevice() {
        return device;
    }

    public void setDevice(Long device) {
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

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    @Override
    public String toString() {
        String deviceName = (this.device != null) ? this.device.toString() : "Unbekanntes Gerät";
        String stateValue = (this.state != null && !this.state.trim().isEmpty()) ? this.state : "KEIN STATUS";
        String timeStamp = (this.time != null) ? this.time.toString() : "Unbekannte Zeit";
        String userName = (this.user != null) ? " (Aktion durch: " + this.user.toString() + ")" : "";

        return "[" + timeStamp + "] " + deviceName + " -> " + stateValue + userName;
    }
}
