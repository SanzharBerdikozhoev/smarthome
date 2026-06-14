package whz.pti.services.implementation;

import whz.pti.models.Device;
import whz.pti.repositories.DeviceRepo;
import whz.pti.services.DeviceService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepo deviceRepo;

    public DeviceServiceImpl(DeviceRepo deviceRepo) {
        this.deviceRepo = deviceRepo;
    }

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

    @Override
    public void updateDeviceState(Long deviceId, boolean active, Long userId) {
        deviceRepo.updateDeviceState(deviceId, active);

        String logSql = "INSERT INTO device_state_log (device_id, user_id, timestamp, state_value) " +
                "VALUES (?, ?, GETDATE(), ?)";

        String stateValue = active ? "ON" : "OFF";

        try (Connection conn = whz.pti.utils.DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(logSql)) {

            stmt.setLong(1, deviceId);
            stmt.setLong(2, userId);
            stmt.setString(3, stateValue);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
