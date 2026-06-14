package whz.pti.repositories.implementation;

import whz.pti.models.User;
import whz.pti.repositories.UserRepo;


public class tUserRepoImpl extends GeneralRepoImpl<User> implements UserRepo {
    public UserRepoImpl() {
        super("users");
    }
}
