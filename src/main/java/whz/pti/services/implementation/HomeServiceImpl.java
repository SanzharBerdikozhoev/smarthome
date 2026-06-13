package whz.pti.services.implementation;

import whz.pti.models.Home;
import whz.pti.repositories.HomeRepo;
import whz.pti.repositories.implementation.HomeRepoImpl;
import whz.pti.services.HomeService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class HomeServiceImpl implements HomeService {
    private HomeRepo homeRepo = new HomeRepoImpl();
    @Override
    public void save(Home home) {
        homeRepo.save(home);
    }

    @Override
    public void update(Home newHome, Home oldHome) {
        homeRepo.update(newHome, oldHome);
    }

    @Override
    public void delete(Home home) {
        homeRepo.delete(home);
    }

    @Override
    public Optional<Home> getHouse(Long houseId) throws SQLException {
        return homeRepo.getById(houseId);
    }

    @Override
    public Home getHouseByName(String houseName) {
        return null;
    }

    @Override
    public List<Home> getHouses() {
        return (List<Home>) homeRepo.getAll();
    }

    @Override
    public List<Home> getHousesByUserId(Long userId) {
        if (userId == null) return new java.util.ArrayList<>();
        return homeRepo.getHousesByUserId(userId);
    }
}
