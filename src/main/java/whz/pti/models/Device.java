package whz.pti.models;

import whz.pti.repositories.implementation.DeviceTypeRepoImpl;
import whz.pti.repositories.implementation.RoomRepoImpl;
import whz.pti.repositories.implementation.ScenarioRepoImpl;
import whz.pti.repositories.implementation.UserRepoImpl;
import whz.pti.utils.annotations.ForeignKey;
import whz.pti.utils.annotations.ManyToMany;

import java.time.LocalDate;
import java.util.List;

public class Device {
    private Long id;
    private String name;
    @ForeignKey(column = "room_id", repoClass = RoomRepoImpl.class)
    private Room room;
    @ForeignKey(column = "device_type_id", repoClass = DeviceTypeRepoImpl.class)
    private DeviceType deviceType;
    private LocalDate installDate;
    private boolean active;
    @ManyToMany(
            joinTable     = "device_user",
            joinColumn    = "device_id",
            inverseColumn = "user_id",
            repoClass     = UserRepoImpl.class
    )
    private List<User> users;

    @ManyToMany(
            joinTable     = "device_scenario",
            joinColumn    = "device_id",
            inverseColumn = "automation_id",
            repoClass     = ScenarioRepoImpl.class
    )
    private List<Scenario> scenarios;

    public Device( ){}

    public Device(Long id, String name, Room room, DeviceType deviceType, LocalDate installDate, boolean active, List<User> users,  List<Scenario> scenarios) {
        this.id = id;
        this.name = name;
        this.room = room;
        this.deviceType = deviceType;
        this.installDate = installDate;
        this.active = active;
        this.users = users;
        this.scenarios = scenarios;
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Scenario> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<Scenario> scenarios) {
        this.scenarios = scenarios;
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



