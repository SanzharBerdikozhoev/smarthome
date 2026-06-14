package whz.pti.repositories.implementation;

import whz.pti.models.DeviceType;
import whz.pti.repositories.DeviceRepo;
import whz.pti.repositories.DeviceTypeRepo;
import whz.pti.repositories.GeneralRepo;

public class DeviceTypeRepoImpl extends GeneralRepoImpl<DeviceType> implements DeviceTypeRepo {
    public DeviceTypeRepoImpl() {super("device_type");}

    @Override
    public DeviceType getDeviceTypeByDeviceId(Long deviceId) {
        DeviceType deviceType = null;

        String sql = "SELECT dt.* FROM device_type dt " +
                "JOIN device d ON dt.id = d.device_type_id " +
                "WHERE d.id = ?";

        try (java.sql.Connection conn = whz.pti.utils.DBConnection.getInstance().getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, deviceId);

            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    deviceType = new DeviceType();
                    deviceType.setId(rs.getLong("id"));
                    deviceType.setName(rs.getString("name"));
                    deviceType.setDescription(rs.getString("description"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deviceType;
    }
}
