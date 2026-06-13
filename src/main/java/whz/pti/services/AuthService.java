package whz.pti.services;

import whz.pti.models.SafeUser;
import whz.pti.models.User;

public interface AuthService {
    void register(User newUser);
    SafeUser login(String username, String password) throws Exception;
}
