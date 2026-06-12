package whz.pti.models;

import java.math.BigDecimal;

public class DeviceUser {
    private BigDecimal id;
    private Device device;
    private User user;

    private DeviceUser() {}

    public DeviceUser(BigDecimal id, Device device, User user) {
        this.id = id;
        this.device = device;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "DeviceUser{" +
                "id=" + id +
                ", device=" + device +
                ", user=" + user +
                '}';
    }
}
