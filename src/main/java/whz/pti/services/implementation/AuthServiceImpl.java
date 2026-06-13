package whz.pti.services.implementation;

import whz.pti.models.User;
import whz.pti.repositories.UserRepo;
import whz.pti.repositories.implementation.UserRepoImpl;
import whz.pti.services.AuthService;
import whz.pti.utils.PasswordService;

public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo = new UserRepoImpl();

    @Override
    public void register(String username, String password) {
        PasswordService.hashPassword(password);
    }

    @Override
    public Long login(String username, String password) {
        User user = userRepo
                .getByField("username", username)
                .orElseThrow(()-> new NullPointerException("Benutzername oder Passwort falsch"));

        boolean passwordMatch = PasswordService.verifyPassword(password, user.getPassword());

        if(!passwordMatch) {
            throw new RuntimeException("Benutzername oder Passwort falsch");
        }

        return user.getId();
    }
}
