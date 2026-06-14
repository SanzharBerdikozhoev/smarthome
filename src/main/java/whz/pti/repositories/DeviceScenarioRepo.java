package whz.pti.repositories;

import whz.pti.models.DeviceScenario;

import java.util.List;

public interface DeviceScenarioRepo extends GeneralRepo<DeviceScenario> {
    List<DeviceScenario> getByScenarioId(Long scenarioId);
}
