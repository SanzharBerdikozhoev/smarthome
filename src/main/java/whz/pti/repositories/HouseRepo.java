package whz.pti.repositories;

import whz.pti.models.Home;

public interface HouseRepo extends GeneralRepo<Home> {
    java.util.List<Home> getHousesByUserId(Long userId);
}
