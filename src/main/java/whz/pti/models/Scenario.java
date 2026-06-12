package whz.pti.models;

import whz.pti.repositories.implementation.DeviceRepoImpl;
import whz.pti.utils.annotations.Column;
import whz.pti.utils.annotations.ManyToMany;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Scenario {
    private Long id;
    @Column(name = "device_id")
    private String deviceName;
    private Boolean isActive;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;

    @ManyToMany(
            joinTable     = "device_scenario",
            joinColumn    = "automation_id",
            inverseColumn = "device_id",
            repoClass     = DeviceRepoImpl.class
    )
    private List<Device> devices;

    private Scenario() {}

    public Scenario(Long id, String deviceName, Boolean isActive ,LocalTime startTime, LocalTime endTime, List<Device> devices) {
        this.id = id;
        this.deviceName = deviceName;
        this.isActive = isActive;
        this.startTime = startTime;
        this.endTime = endTime;
        this.devices = devices;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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


    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {this.isActive = isActive;}

    @Override
    public String toString() {
        return "Scenario{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
