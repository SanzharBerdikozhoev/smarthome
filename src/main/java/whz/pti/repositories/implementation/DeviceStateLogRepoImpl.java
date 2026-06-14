package whz.pti.repositories.implementation;

import whz.pti.models.DeviceLogState;
import whz.pti.models.DeviceStateLog;
import whz.pti.repositories.DeviceStateLogRepo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DeviceStateLogRepoImpl extends GeneralRepoImpl<DeviceStateLog> implements DeviceStateLogRepo {
    public DeviceStateLogRepoImpl() {super("device_state_log");}

    @Override
    public List<DeviceStateLog> getLogsByRoomId(Long roomId) {
        List<DeviceStateLog> logs = new ArrayList<>();

        String sql = "SELECT dsl.id AS log_id, dsl.timestamp, dsl.state_value, " +
                "       dsl.device_id, dsl.user_id " +
                "FROM device_state_log dsl " +
                "JOIN device d ON dsl.device_id = d.id " +
                "WHERE d.room_id = ? " +
                "ORDER BY dsl.timestamp DESC";

        try (Connection conn = whz.pti.utils.DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, roomId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DeviceStateLog log = new DeviceStateLog();
                    log.setId(rs.getLong("log_id"));
                    log.setState(DeviceLogState.valueOf(rs.getString("state_value")));

                    if (rs.getTimestamp("timestamp") != null) {
                        log.setTime(rs.getTimestamp("timestamp").toLocalDateTime());
                    }

                    log.setDevice(rs.getLong("device_id"));
                    log.setUser(rs.getLong("user_id"));

                    logs.add(log);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(logs);
        return logs;
    }
}
