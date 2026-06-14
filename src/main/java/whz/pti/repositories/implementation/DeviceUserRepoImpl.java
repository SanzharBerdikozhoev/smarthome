package whz.pti.repositories.implementation;

import whz.pti.models.DeviceUser;
import whz.pti.repositories.DeviceUserRepo;

public class DeviceUserRepoImpl extends GeneralRepoImpl<DeviceUser> implements DeviceUserRepo {
    public DeviceUserRepoImpl() {super("device_user");}
}
