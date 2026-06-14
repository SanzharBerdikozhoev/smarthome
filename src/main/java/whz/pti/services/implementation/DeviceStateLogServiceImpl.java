package whz.pti.services.implementation;

import whz.pti.models.DeviceStateLog;
import whz.pti.repositories.DeviceStateLogRepo;
import whz.pti.repositories.implementation.DeviceStateLogRepoImpl;
import whz.pti.services.DeviceStateLogService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DeviceStateLogServiceImpl implements DeviceStateLogService {
    private DeviceStateLogRepo deviceStateLogRepo =  new DeviceStateLogRepoImpl();

    @Override
    public void save(DeviceStateLog deviceStateLog) {
        deviceStateLogRepo.save(deviceStateLog);
    }

    @Override
    public void update(DeviceStateLog newDeviceStateLog, DeviceStateLog oldDeviceStateLog) {
        deviceStateLogRepo.update(newDeviceStateLog, oldDeviceStateLog);
    }

    @Override
    public void delete(DeviceStateLog deviceStateLog) {
        deviceStateLogRepo.delete(deviceStateLog);
    }

    @Override
    public Optional<DeviceStateLog> getDeviceStateLog(Long deviceStateLogId) throws SQLException {
        return deviceStateLogRepo.getById(deviceStateLogId);
    }

    @Override
    public List<DeviceStateLog> getDeviceStateLogs() {
        return (List<DeviceStateLog>)  deviceStateLogRepo.getAll();
    }

    @Override
    public List<DeviceStateLog> getLogsByRoomId(Long roomId) {
        return deviceStateLogRepo.getLogsByRoomId(roomId);
    }
}
