package whz.pti.models;

import java.time.LocalDate;

public class Device {
    private Long id;
    private String name;
    private Room room;
    private DeviceType deviceType;
    private LocalDate installDate;
    private boolean active;

    public Device( ){}

    public Device(Long id, String name, Room room, DeviceType deviceType, LocalDate installDate, boolean active) {
        this.id = id;
        this.name = name;
        this.room = room;
        this.deviceType = deviceType;
        this.installDate = installDate;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public LocalDate getInstallDate() {
        return installDate;
    }

    public void setInstallDate(LocalDate installDate) {
        this.installDate = installDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", room=" + room +
                ", deviceType=" + deviceType +
                ", installDate=" + installDate +
                ", active=" + active +
                '}';
    }
}



