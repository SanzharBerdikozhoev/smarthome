package whz.pti.repositories.implementation;

import whz.pti.models.DeviceType;
import whz.pti.repositories.DeviceRepo;
import whz.pti.repositories.DeviceTypeRepo;
import whz.pti.repositories.GeneralRepo;

public class DeviceTypeRepoImpl extends GeneralRepoImpl<DeviceType> implements DeviceTypeRepo {
    public DeviceTypeRepoImpl() {super("device_type");}
}
