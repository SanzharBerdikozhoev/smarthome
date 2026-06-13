package whz.pti.repositories;

import whz.pti.models.Room;

public interface RoomRepo extends GeneralRepo<Room> {
    java.util.List<Room> getRoomsByHouseId(Long houseId);
}
