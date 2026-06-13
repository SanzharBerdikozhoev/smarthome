package whz.pti.services.implementation;

import whz.pti.models.SafeUser;
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
    public SafeUser login(String username, String password) throws Exception {
        User user = userRepo
                .getByField("username", username)
                .orElseThrow(()-> new Exception("Benutzername oder Passwort falsch"));

        System.out.println(user);

        boolean passwordMatch = PasswordService.verifyPassword(password, user.getPassword());

        if(!passwordMatch) {
            throw new Exception("Benutzername oder Passwort falsch");
        }

        return new SafeUser(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

//    @Override
//    public SafeUser getUser(Long userId) {
//        Optional<User> result = userRepo.getById(userId);
//        SafeUser user = new SafeUser();
//
//        result.ifPresent(u -> {
//            user.setName(u.getName());
//            user.setEmail(u.getEmail());
//            user.setRole(u.getRole());
//        });
//
//        return user;
//    }
}
