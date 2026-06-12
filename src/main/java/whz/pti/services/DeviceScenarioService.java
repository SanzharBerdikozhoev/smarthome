package whz.pti.services;

import whz.pti.models.DeviceScenario;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DeviceScenarioService {
    void save(DeviceScenario deviceScenario);
    void update(DeviceScenario newDeviceScenario, DeviceScenario oldDeviceScenario);
    void delete(DeviceScenario deviceScenario);
    Optional<DeviceScenario> getDeviceScenario(Long deviceScenarioId) throws SQLException;
    List<DeviceScenario> getDeviceScenarios();
}
