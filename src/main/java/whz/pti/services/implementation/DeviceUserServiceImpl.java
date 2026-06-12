package whz.pti.services.implementation;

import whz.pti.models.DeviceUser;
import whz.pti.repositories.DeviceUserRepo;
import whz.pti.repositories.implementation.DeviceUserRepoImpl;
import whz.pti.services.DeviceUserService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DeviceUserServiceImpl implements DeviceUserService {
    private DeviceUserRepo deviceUserRepo = new DeviceUserRepoImpl();

    @Override
    public void save(DeviceUser deviceUser) {
        deviceUserRepo.save(deviceUser);
    }

    @Override
    public void update(DeviceUser newDeviceUser, DeviceUser oldDeviceUser) {
        deviceUserRepo.update(newDeviceUser, oldDeviceUser);
    }

    @Override
    public void delete(DeviceUser deviceUser) {
        deviceUserRepo.delete(deviceUser);
    }

    @Override
    public Optional<DeviceUser> getDeviceUserById(Long deviceUserId) throws SQLException {
        return deviceUserRepo.getById(deviceUserId);
    }

    @Override
    public DeviceUser getDeviceUserByName(String deviceUserName) {
        return null;
    }

    @Override
    public List<DeviceUser> getDeviceUsers() {
        return (List<DeviceUser>) deviceUserRepo.getAll();
    }
}
