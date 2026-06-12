package whz.pti.services;

import whz.pti.models.DeviceUser;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DeviceUserService {
    void save(DeviceUser deviceUser);
    void update(DeviceUser newDeviceUser,  DeviceUser oldDeviceUser);
    void delete(DeviceUser deviceUser);
    Optional<DeviceUser> getDeviceUserById(Long deviceUserId) throws SQLException;
    DeviceUser getDeviceUserByName(String deviceUserName);
    List<DeviceUser> getDeviceUsers();
}
