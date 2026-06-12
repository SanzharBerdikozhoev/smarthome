package whz.pti.models;

import whz.pti.repositories.implementation.DeviceRepoImpl;
import whz.pti.repositories.implementation.UserRepoImpl;
import whz.pti.utils.annotations.Column;
import whz.pti.utils.annotations.ForeignKey;

import java.time.LocalDateTime;

public class DeviceStateLog {
    private Long id;
    @ForeignKey(column = "device_id", repoClass = DeviceRepoImpl.class)
    private Device device;
    @Column(name = "timestamp")
    private LocalDateTime time;
    @Column(name = "state_value")
    private String state;
    @ForeignKey(column = "user_id", repoClass = UserRepoImpl.class)
    private User user;

    public DeviceStateLog() {}

    public DeviceStateLog(Long id, Device device, LocalDateTime time, String state, User user) {
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
