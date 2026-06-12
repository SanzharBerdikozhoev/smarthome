package whz.pti.models;

public class DeviceUser {
    private Device device;
    private User user;

    private DeviceUser() {}

    public DeviceUser(Long id, Device device, User user) {
        this.device = device;
        this.user = user;
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
                ", device=" + device +
                ", user=" + user +
                '}';
    }
}
