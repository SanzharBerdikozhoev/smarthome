package whz.pti.services.implementation;

import whz.pti.models.User;
import whz.pti.repositories.UserRepo;
import whz.pti.repositories.implementation.UserRepoImpl;
import whz.pti.services.AuthService;
import whz.pti.utils.PasswordService;

public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo = new UserRepoImpl();

    @Override
    public void register(User newUser) {
        boolean userExists = userRepo.getByField("email", newUser.getEmail()).isPresent();
        if(userExists) {
            throw new RuntimeException("Benutzer existiert bereits");
        }

        String hashedPassword = PasswordService.hashPassword(newUser.getPassword());
        newUser.setPassword(hashedPassword);

        userRepo.save(newUser);
    }

    @Override
    public Long login(String username, String password) throws RuntimeException {
        User user = userRepo
                .getByField("username", username)
                .orElseThrow(()-> new RuntimeException("Benutzername oder Passwort falsch"));

        System.out.println(user);

        boolean passwordMatch = PasswordService.verifyPassword(password, user.getPassword());

        if(!passwordMatch) {
            throw new RuntimeException("Benutzername oder Passwort falsch");
        }

        return user.getId();
    }
}
