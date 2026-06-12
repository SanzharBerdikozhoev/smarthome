package whz.pti.services;

import whz.pti.models.DeviceType;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DeviceTypeService {
    void save(DeviceType deviceType);
    void update(DeviceType newDeviceType,  DeviceType oldDeviceType);
    void delete(DeviceType deviceType);
    Optional<DeviceType> getDeviceType(Long deviceTypeId) throws SQLException;
    DeviceType getDeviceTypeByName(String deviceTypeName);
    List<DeviceType> getDeviceTypes();
}
