package whz.pti.services.implementation;

import whz.pti.repositories.UserRepo;
import whz.pti.repositories.implementation.UserRepoImpl;
import whz.pti.services.AuthService;
import whz.pti.utils.PasswordService;


public class AuthServiceImpl implements AuthService {
    private UserRepo userRepo = new UserRepoImpl("users");

    @Override
    public void register(String username, String password) {
        PasswordService.hashPassword(password);
    }

    @Override
    public void login(String username, String password) {
        PasswordService.verifyPassword(password, "");
    }
}
