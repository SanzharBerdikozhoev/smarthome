package whz.pti.services.implementation;

import whz.pti.models.Home;
import whz.pti.repositories.HouseRepo;
import whz.pti.repositories.implementation.HouseRepoImpl;
import whz.pti.services.HomeService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class HomeServiceImpl implements HomeService {
    private HouseRepo houseRepo = new HouseRepoImpl();
    @Override
    public void save(Home home) {
        houseRepo.save(home);
    }

    @Override
    public void update(Home newHome, Home oldHome) {
        houseRepo.update(newHome, oldHome);
    }

    @Override
    public void delete(Home home) {
        houseRepo.delete(home);
    }

    @Override
    public Optional<Home> getHouse(Long houseId) throws SQLException {
        return houseRepo.getById(houseId);
    }

    @Override
    public Home getHouseByName(String houseName) {
        return null;
    }

    @Override
    public List<Home> getHouses() {
        return (List<Home>) houseRepo.getAll();
    }

    @Override
    public List<Home> getHousesByUserId(Long userId) {
        if (userId == null) return new java.util.ArrayList<>();
        return houseRepo.getHousesByUserId(userId);
    }
}
