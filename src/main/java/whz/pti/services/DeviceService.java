package whz.pti.services;

import whz.pti.models.Device;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DeviceService {
    void save(Device device);
    void update(Device newDevice, Device oldDevice);
    void delete(Device device);
    Optional<Device> getDevice(Long deviceId) throws SQLException;
    Device getDeviceByName(String deviceName);
    List<Device> getDevices();
}
