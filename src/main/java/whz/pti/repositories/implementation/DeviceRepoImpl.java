package whz.pti.repositories.implementation;

import whz.pti.models.Device;
import whz.pti.repositories.DeviceRepo;

import java.util.List;

public class DeviceRepoImpl extends GeneralRepoImpl<Device> implements DeviceRepo {
    public DeviceRepoImpl() {super("device");}

    @Override
    public java.util.List<Device> getDevicesByRoomId(Long roomId) {
        java.util.List<Device> devices = new java.util.ArrayList<>();
        String sql = "SELECT * FROM device WHERE room_id = ?";
        try (java.sql.Connection conn = whz.pti.utils.DBConnection.getInstance().getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, roomId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Device device = new Device();
                    device.setId(rs.getLong("id"));
                    device.setName(rs.getString("name"));
                    device.setActive(rs.getBoolean("is_active"));
                    devices.add(device);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return devices;
    }

    @Override
    public void updateDeviceState(Long deviceId, boolean active) {
        String sql = "UPDATE device SET is_active = ? WHERE id = ?";
        try (java.sql.Connection conn = whz.pti.utils.DBConnection.getInstance().getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, active);
            stmt.setLong(2, deviceId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
