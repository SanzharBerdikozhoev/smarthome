package whz.pti.services.implementation;

import whz.pti.models.Scenario;
import whz.pti.repositories.ScenarioRepo;
import whz.pti.repositories.implementation.ScenarioRepoImpl;
import whz.pti.services.ScenarioService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ScenarioServiceImpl implements ScenarioService {
    private ScenarioRepo scenarioRepo = new ScenarioRepoImpl();

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
}
