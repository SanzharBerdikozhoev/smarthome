package whz.pti.utils;

import whz.pti.models.Role;
import whz.pti.models.User;
import whz.pti.repositories.UserRepo;

import java.util.List;

public class UserInitializer {
    public static void init() {
        UserRepo userRepo = AppContext.getInstance().getUserRepo();

        List<User> users = List.of(
           new User("admin", "admin@smarthome.com", Role.ADMIN, "admin123"),
           new User("john", "john@gmail.com", Role.WRITER, "writer123"),
           new User("anna", "anna@gmail.com", Role.READER, "reader123")
        );

        users.forEach(user -> {
            var existingUser = userRepo.getByField("email", user.getEmail());

            if (existingUser.isEmpty()) {
                userRepo.save(user);
                System.out.println("Initialer Benutzer hinzugefügt: " + user.getEmail());
            }
        });
    }
}
