package whz.pti.repositories.implementation;

import whz.pti.models.Scenario;
import whz.pti.repositories.ScenarioRepo;
import whz.pti.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ScenarioRepoImpl extends GeneralRepoImpl<Scenario> implements ScenarioRepo {
    public ScenarioRepoImpl() {super();}

    @Override
    public List<Scenario> getScenariosByUserId(Long userId) {
        List<Scenario> scenarios = new ArrayList<>();
        String sql = "SELECT DISTINCT s.* FROM scenario s " +
                "JOIN device_scenario ds ON s.id = ds.automation_id " +
                "JOIN device d ON ds.device_id = d.id " +
                "JOIN room r ON d.room_id = r.id " +
                "JOIN home h ON r.home_id = h.id " +
                "WHERE h.user_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Scenario scenario = new Scenario(
                            rs.getLong("id"),
                            rs.getString("device_id"),
                            rs.getBoolean("is_active"),
                            rs.getTime("start_time").toLocalTime(),
                            rs.getTime("end_time").toLocalTime(),
                            new ArrayList<>()
                    );
                    scenarios.add(scenario);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scenarios;
    }
}
