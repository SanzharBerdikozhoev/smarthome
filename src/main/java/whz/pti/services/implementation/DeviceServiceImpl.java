package whz.pti.services.implementation;

import whz.pti.models.Device;
import whz.pti.repositories.DeviceRepo;
import whz.pti.repositories.implementation.DeviceRepoImpl;
import whz.pti.services.DeviceService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DeviceServiceImpl implements DeviceService {
    private DeviceRepo deviceRepo = new DeviceRepoImpl();

    @Override
    public void save(Device device) {
        deviceRepo.save(device);
    }

    @Override
    public void update(Device newDevice, Device oldDevice) {
        deviceRepo.update(newDevice, oldDevice);
    }

    @Override
    public void delete(Device device) {
        deviceRepo.delete(device);
    }

    @Override
    public Optional<Device> getDevice(Long deviceId) throws SQLException {
        return deviceRepo.getById(deviceId);
    }

    @Override
    public Device getDeviceByName(String deviceName) {
        return null;
    }

    @Override
    public List<Device> getDevices() {
        return (List<Device>)  deviceRepo.getAll();
    }

    @Override
    public List<Device> getDevicesByRoomId(Long roomId) {
        if (roomId == null) return new java.util.ArrayList<>();
        return deviceRepo.getDevicesByRoomId(roomId);
    }
}
