package whz.pti.services;

import whz.pti.models.User;

public interface AuthService {
    void register(User newUser);
    Long login(String username, String password);
}
