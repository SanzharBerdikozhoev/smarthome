package whz.pti.models;

import whz.pti.repositories.implementation.DeviceRepoImpl;
import whz.pti.repositories.implementation.UserRepoImpl;
import whz.pti.utils.annotations.ForeignKey;

import java.time.LocalDate;

public class DeviceUser {
    @ForeignKey(column = "device_id", repoClass = DeviceRepoImpl.class)
    private Device device;
    @ForeignKey(column = "user_id", repoClass = UserRepoImpl.class)
    private User user;
    private LocalDate assignedSince;

    public DeviceUser() {}

    public DeviceUser(Long id, Device device, User user, LocalDate assignedSince) {
        this.device = device;
        this.user = user;
        this.assignedSince = assignedSince;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getAssignedSince() {
        return assignedSince;
    }

    public void setAssignedSince(LocalDate assignedSince) {
        this.assignedSince = assignedSince;
    }

    @Override
    public String toString() {
        return "DeviceUser{" +
                ", device=" + device +
                ", user=" + user +
                '}';
    }
}
