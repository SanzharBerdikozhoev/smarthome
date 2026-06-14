package whz.pti.services;

import whz.pti.models.DeviceStateLog;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DeviceStateLogService {
    void save(DeviceStateLog deviceStateLog);
    void update(DeviceStateLog newDeviceStateLog,  DeviceStateLog oldDeviceStateLog);
    void delete(DeviceStateLog deviceStateLog);
    Optional<DeviceStateLog> getDeviceStateLog(Long deviceStateLogId) throws SQLException;
    List<DeviceStateLog> getDeviceStateLogs();
    List<DeviceStateLog> getLogsByRoomId(Long roomId);
}
