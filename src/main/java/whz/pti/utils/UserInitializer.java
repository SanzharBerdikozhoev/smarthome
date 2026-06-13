package whz.pti.utils;

import whz.pti.models.Role;
import whz.pti.models.User;
import whz.pti.services.AuthService;
import whz.pti.services.implementation.AuthServiceImpl;

import java.util.List;

public class UserInitializer {
    public static void init() {
        AuthService authService = new AuthServiceImpl();

        List<User> users = List.of(
           new User("admin", "admin@smarthome.com", Role.ADMIN, "admin123"),
           new User("john", "john@gmail.com", Role.WRITER, "writer123"),
           new User("anna", "anna@gmail.com", Role.READER, "reader123")
        );

        users.forEach(authService::register);
    }
}
