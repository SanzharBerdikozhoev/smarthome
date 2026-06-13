package whz.pti.repositories;

import whz.pti.models.Home;

public interface HomeRepo extends GeneralRepo<Home> {
    java.util.List<Home> getHousesByUserId(Long userId);
}
