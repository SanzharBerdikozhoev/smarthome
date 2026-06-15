package whz.pti.services.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import whz.pti.models.Device;
import whz.pti.models.DeviceScenario;
import whz.pti.models.DeviceScenarioRole;
import whz.pti.models.Scenario;
import whz.pti.repositories.ScenarioRepo;
import whz.pti.repositories.implementation.DeviceRepoImpl;
import whz.pti.repositories.implementation.ScenarioRepoImpl;
import whz.pti.services.DeviceScenarioService;
import whz.pti.services.DeviceService;
import whz.pti.services.ScenarioService;
import whz.pti.utils.AppContext;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ScenarioServiceImpl implements ScenarioService {
    private ScenarioRepo scenarioRepo = new ScenarioRepoImpl();
    private final DeviceScenarioService deviceScenarioService = new DeviceScenarioServiceImpl();

    private final DeviceService deviceService =
            new DeviceServiceImpl(new DeviceRepoImpl());

    @Override
    public void save(Scenario scenario) {
        scenarioRepo.save(scenario);
    }

    @Override
    public void update(Scenario newScenario, Scenario oldScenario) {
        scenarioRepo.update(newScenario, oldScenario);
    }

    @Override
    public void delete(Scenario scenario) {
        scenarioRepo.delete(scenario);
    }

    @Override
    public Optional<Scenario> getScenario(Long scenarioId) throws SQLException {
        return scenarioRepo.getById(scenarioId);
    }

    @Override
    public List<Scenario> getScenarios() {
        return (List<Scenario>) scenarioRepo.getAll();
    }

    @Override
    public List<Scenario> getScenariosByUserId(Long userId) {
        if (userId == null) return new java.util.ArrayList<>();
        return scenarioRepo.getScenariosByUserId(userId);
    }

    @Override
    public void executeScenario(
            Scenario scenario,
            Long userId
    ) {
        List<DeviceScenario> deviceScenarios =
                deviceScenarioService.getByScenarioId(
                        scenario.getId()
                );


        for (DeviceScenario ds : deviceScenarios) {

            if (!DeviceScenarioRole.OUTPUT.equals(ds.getRole())) {
                 continue;
            }

            Device device = ds.getDevice();

            if (device == null) {
                continue;
            }

            deviceService.updateDeviceState(
                    device.getId(),
                    scenario.getIsActive(),
                    userId
            );
        }

    }


}
