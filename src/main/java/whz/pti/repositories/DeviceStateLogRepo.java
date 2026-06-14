package whz.pti.repositories;

import whz.pti.models.DeviceStateLog;

import java.util.List;

public interface DeviceStateLogRepo extends GeneralRepo<DeviceStateLog> {
    List<DeviceStateLog> getLogsByRoomId(Long roomId);
}
