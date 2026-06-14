package whz.pti.services.implementation;

import whz.pti.models.DeviceScenario;
import whz.pti.repositories.DeviceScenarioRepo;
import whz.pti.repositories.implementation.DeviceScenarioRepoImpl;
import whz.pti.services.DeviceScenarioService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DeviceScenarioServiceImpl implements DeviceScenarioService {
    private DeviceScenarioRepo deviceScenarioRepo = new DeviceScenarioRepoImpl();

    @Override
    public void save(DeviceScenario deviceScenario) {
        deviceScenarioRepo.save(deviceScenario);
    }

    @Override
    public void update(DeviceScenario newDeviceScenario, DeviceScenario oldDeviceScenario) {
        deviceScenarioRepo.update(newDeviceScenario, oldDeviceScenario);
    }

    @Override
    public void delete(DeviceScenario deviceScenario) {
        deviceScenarioRepo.delete(deviceScenario);
    }

    @Override
    public Optional<DeviceScenario> getDeviceScenario(Long deviceScenarioId) throws SQLException {
        return deviceScenarioRepo.getById(deviceScenarioId);
    }

    @Override
    public List<DeviceScenario> getDeviceScenarios() {
        return (List<DeviceScenario>) deviceScenarioRepo.getAll();
    }

    @Override
    public List<DeviceScenario> getByScenarioId(Long scenarioId) {
        return deviceScenarioRepo.getByScenarioId(scenarioId);
    }
}
