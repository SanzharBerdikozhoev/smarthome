package whz.pti.services;

import whz.pti.models.Room;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RoomService {
    void save(Room room);
    void update(Room newRoom, Room oldRoom);
    void delete(Room room);
    Optional<Room> getRoom(Long roomId) throws SQLException;
    Room getRoomByName(String roomName);
    List<Room> getRooms();
    List<Room> getRoomsByHouseId(Long houseId);
}
