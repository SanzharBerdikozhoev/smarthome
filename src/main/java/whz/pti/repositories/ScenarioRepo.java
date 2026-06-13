package whz.pti.repositories;

import whz.pti.models.Scenario;

import java.util.List;

public interface ScenarioRepo extends GeneralRepo<Scenario> {
    List<Scenario> getScenariosByUserId(Long userId);
}
