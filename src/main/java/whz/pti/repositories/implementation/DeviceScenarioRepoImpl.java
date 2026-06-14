package whz.pti.repositories.implementation;

import whz.pti.models.DeviceScenario;
import whz.pti.repositories.DeviceScenarioRepo;

public class DeviceScenarioRepoImpl extends GeneralRepoImpl<DeviceScenario> implements DeviceScenarioRepo {
    public DeviceScenarioRepoImpl() {super("device_scenario");}
}
