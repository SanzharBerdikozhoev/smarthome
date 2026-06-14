package whz.pti.models;

import whz.pti.repositories.implementation.DeviceRepoImpl;
import whz.pti.utils.annotations.Column;
import whz.pti.utils.annotations.ManyToMany;

import java.time.LocalTime;
import java.util.List;

public class Scenario {
    private Long id;
    private String name;
    private String description;
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

    public Scenario() {}

    public Scenario(Long id, String name, Boolean isActive ,LocalTime startTime, LocalTime endTime, List<Device> devices) {
        this.id = id;
        this.name = name;
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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        String start = (this.startTime != null) ? this.startTime.toString() : "00:00";
        String end = (this.endTime != null) ? this.endTime.toString() : "23:59";
        String displayId = (this.id != null) ? String.valueOf(this.id) : "Neu";

        return "Szenario (ID: " + displayId + ") [" + start + " - " + end + "]";
    }
}
