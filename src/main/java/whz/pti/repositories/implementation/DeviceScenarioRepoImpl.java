package whz.pti.repositories.implementation;

import whz.pti.models.Device;
import whz.pti.models.DeviceScenario;
import whz.pti.models.DeviceScenarioRole;
import whz.pti.repositories.DeviceRepo;
import whz.pti.repositories.DeviceScenarioRepo;
import whz.pti.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DeviceScenarioRepoImpl extends GeneralRepoImpl<DeviceScenario> implements DeviceScenarioRepo {
    public DeviceScenarioRepoImpl() {super("device_scenario");}

    @Override
    public List<DeviceScenario> getByScenarioId(Long scenarioId) {

        List<DeviceScenario> result = new ArrayList<>();

        String sql = """
                SELECT *
                FROM device_scenario
                WHERE automation_id = ?
        """;

        try (
                Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setLong(1, scenarioId);

            ResultSet rs = stmt.executeQuery();

            DeviceRepo deviceRepo = new DeviceRepoImpl();

            while (rs.next()) {
                Long deviceId = rs.getLong("device_id");
                Device device = deviceRepo.getById(deviceId).orElse(null);

                DeviceScenario ds = new DeviceScenario();
                ds.setDevice(device);

                String roleStr = rs.getString("role");
                if (roleStr != null) {
                    try {
                        ds.setRole(DeviceScenarioRole.valueOf(roleStr.trim()));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Warnung: Unbekannter Enum-Wert in DB: " + roleStr);
                        ds.setRole(null);
                    }
                } else {
                    ds.setRole(null);
                }

                result.add(ds);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
