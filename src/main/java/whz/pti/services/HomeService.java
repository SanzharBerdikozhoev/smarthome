package whz.pti.services;

import whz.pti.models.Home;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface HomeService {
    void save(Home home);
    void update(Home newHome, Home oldHome);
    void delete(Home home);
    Optional<Home> getHouse(Long houseId) throws SQLException;
    Home getHouseByName(String houseName);
    List<Home> getHouses();
    List<Home> getHousesByUserId(Long userId);
}
