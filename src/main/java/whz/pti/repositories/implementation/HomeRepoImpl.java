package whz.pti.repositories.implementation;

import whz.pti.models.Home;
import whz.pti.repositories.HomeRepo;

public class HomeRepoImpl extends GeneralRepoImpl<Home> implements HomeRepo {
    public HomeRepoImpl() {
        super("home");
    }

    @Override
    public java.util.List<Home> getHousesByUserId(Long userId) {
        java.util.List<Home> houses = new java.util.ArrayList<>();
        String sql = "SELECT * FROM home WHERE user_id = ?";
        try (java.sql.Connection conn = whz.pti.utils.DBConnection.getInstance().getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Home home = new Home();
                    home.setId(rs.getLong("id"));
                    home.setAddress(rs.getString("address"));
                    home.setTown(rs.getString("town"));
                    home.setZipCode(rs.getString("zipCode"));
                    houses.add(home);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return houses;
    }
}

