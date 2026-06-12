package whz.pti.repositories.implementation;

import whz.pti.models.User;
import whz.pti.repositories.UserRepo;

import java.sql.ResultSet;
import java.util.Map;
import java.util.function.Function;


public class UserRepoImpl extends GeneralRepoImpl<User> implements UserRepo {
    public UserRepoImpl(String tableName, Function<ResultSet, User> rowMapper, Function<User, Map<String, Object>> rowUnmapper) {
        super(tableName, rowMapper, rowUnmapper);
    }
}
