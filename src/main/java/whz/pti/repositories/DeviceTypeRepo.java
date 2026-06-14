package whz.pti.repositories;

import whz.pti.models.DeviceType;

public interface DeviceTypeRepo extends GeneralRepo<DeviceType> {
    DeviceType getDeviceTypeByDeviceId(Long deviceId);
}
