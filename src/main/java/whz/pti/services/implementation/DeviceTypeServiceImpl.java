package whz.pti.services.implementation;

import whz.pti.models.DeviceType;
import whz.pti.repositories.DeviceTypeRepo;
import whz.pti.repositories.implementation.DeviceTypeRepoImpl;
import whz.pti.services.DeviceTypeService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DeviceTypeServiceImpl implements DeviceTypeService {
    private DeviceTypeRepo deviceTypeRepo = new DeviceTypeRepoImpl();

    @Override
    public void save(DeviceType deviceType) {
        deviceTypeRepo.save(deviceType);
    }

    @Override
    public void update(DeviceType newDeviceType, DeviceType oldDeviceType) {
        deviceTypeRepo.update(newDeviceType, oldDeviceType);
    }

    @Override
    public void delete(DeviceType deviceType) {
        deviceTypeRepo.delete(deviceType);
    }

    @Override
    public Optional<DeviceType> getDeviceTypeById(Long deviceTypeId) throws SQLException {
        return deviceTypeRepo.getById(deviceTypeId);
    }

    @Override
    public DeviceType getDeviceTypeByName(String deviceTypeName) {
        return null;
    }

    @Override
    public List<DeviceType> getDeviceTypes() {
        return (List<DeviceType>) deviceTypeRepo.getAll();
    }
}
