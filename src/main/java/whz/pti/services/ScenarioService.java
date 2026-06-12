package whz.pti.services;

import whz.pti.models.Scenario;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ScenarioService {
    void save(Scenario scenario);
    void update(Scenario newScenario, Scenario oldScenario);
    void delete(Scenario scenario);
    Optional<Scenario> getScenarioById(Long scenarioId) throws SQLException;
    Scenario getScenarioByName(String scenarioName);
    List<Scenario> getScenarios();
}
