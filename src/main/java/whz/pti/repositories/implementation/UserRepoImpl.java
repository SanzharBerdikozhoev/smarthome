package whz.pti.repositories.implementation;

import whz.pti.models.User;
import whz.pti.repositories.UserRepo;


public class UserRepoImpl extends GeneralRepoImpl<User> implements UserRepo {
    public UserRepoImpl() {
        super("users");
    }
}
