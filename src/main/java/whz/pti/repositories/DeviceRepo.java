package whz.pti.repositories;

import whz.pti.models.Device;

public interface DeviceRepo extends GeneralRepo<Device> {
    java.util.List<Device> getDevicesByRoomId(Long roomId);
}
