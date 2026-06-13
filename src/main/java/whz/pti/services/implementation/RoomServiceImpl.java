package whz.pti.services.implementation;

import whz.pti.models.Room;
import whz.pti.repositories.RoomRepo;
import whz.pti.repositories.implementation.RoomRepoImpl;
import whz.pti.services.RoomService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class RoomServiceImpl implements RoomService {
    private RoomRepo roomRepo = new RoomRepoImpl();
    @Override
    public void save(Room room) {
        roomRepo.save(room);
    }

    @Override
    public void update(Room newRoom, Room oldRoom) {
        roomRepo.update(newRoom, oldRoom);
    }

    @Override
    public void delete(Room room) {
        roomRepo.delete(room);
    }

    @Override
    public Optional<Room> getRoom(Long roomId) throws SQLException {
        return roomRepo.getById(roomId);
    }

    @Override
    public Room getRoomByName(String roomName) {
        return null;
    }

    @Override
    public List<Room> getRooms() {
        return (List<Room>) roomRepo.getAll();
    }

    @Override
    public List<Room> getRoomsByHouseId(Long houseId) {
        if (houseId == null) return new java.util.ArrayList<>();
        return roomRepo.getRoomsByHouseId(houseId);
    }
}
