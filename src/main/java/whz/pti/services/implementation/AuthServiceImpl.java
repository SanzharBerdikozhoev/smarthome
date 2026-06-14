package whz.pti.services.implementation;

import whz.pti.models.SafeUser;
import whz.pti.models.User;
import whz.pti.repositories.UserRepo;
import whz.pti.services.AuthService;
import whz.pti.utils.PasswordService;

import java.util.Optional;

public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;

    public AuthServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

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
    public SafeUser login(String username, String password) throws Exception {
        User user = userRepo
                .getByField("username", username)
                .orElseThrow(()-> new Exception("Benutzername oder Passwort falsch"));

        boolean passwordMatch = PasswordService.verifyPassword(password, user.getPassword());

        if(!passwordMatch) {
            throw new Exception("Benutzername oder Passwort falsch");
        }

        return new SafeUser(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }


    @Override
    public Optional<User> getUser(Long userId) {
        Optional<User> user = userRepo.getById(userId);
        return user;
    }
}
