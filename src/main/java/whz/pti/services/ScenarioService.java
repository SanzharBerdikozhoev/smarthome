package whz.pti.services;

import whz.pti.models.Scenario;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ScenarioService {
    void save(Scenario scenario);
    void update(Scenario newScenario, Scenario oldScenario);
    void delete(Scenario scenario);
    Optional<Scenario> getScenario(Long scenarioId) throws SQLException;
    List<Scenario> getScenarios();
    List<Scenario> getScenariosByUserId(Long userId);
    void executeScenario(Scenario scenario, Long userId);
}
