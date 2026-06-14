package whz.pti.services;

import whz.pti.models.SafeUser;
import whz.pti.models.User;

import java.util.Optional;

public interface AuthService {
    void register(User newUser);
    SafeUser login(String username, String password) throws Exception;
    Optional<User> getUser(Long user_id);
}
