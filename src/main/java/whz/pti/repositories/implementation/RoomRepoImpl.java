package whz.pti.repositories.implementation;

import whz.pti.models.Room;
import whz.pti.repositories.GeneralRepo;
import whz.pti.repositories.RoomRepo;

import java.util.List;

public class RoomRepoImpl extends GeneralRepoImpl<Room> implements RoomRepo {
    public RoomRepoImpl() {super();}

    @Override
    public java.util.List<Room> getRoomsByHouseId(Long houseId) {
        java.util.List<Room> rooms = new java.util.ArrayList<>();
        String sql = "SELECT * FROM room WHERE home_id = ?";
        try (java.sql.Connection conn = whz.pti.utils.DBConnection.getInstance().getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, houseId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getLong("id"));
                    room.setName(rs.getString("name"));
                    room.setFloor(rs.getString("floor"));
                    room.setSquare(rs.getDouble("square"));
                    rooms.add(room);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }
}
