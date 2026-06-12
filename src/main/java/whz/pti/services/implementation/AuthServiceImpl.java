package whz.pti.services.implementation;

import whz.pti.models.Role;
import whz.pti.models.User;
import whz.pti.repositories.UserRepo;
import whz.pti.repositories.implementation.UserRepoImpl;
import whz.pti.services.AuthService;
import whz.pti.utils.PasswordService;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class AuthServiceImpl implements AuthService {
    private UserRepo userRepo = new UserRepoImpl(
            "users",

            // (ResultSet -> User)
            rs -> {
                try {
                    return new User(
                            rs.getBigDecimal("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            Role.valueOf(rs.getString("role")),
                            rs.getString("password_hash")
                    );
                } catch (SQLException e) {
                    throw new RuntimeException("Fehler beim Mapping des Benutzers", e);
                }
            },

            // (User -> Map)
            user -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", user.getId());
                map.put("username", user.getName());
                map.put("password_hash", user.getPassword());
                map.put("role", user.getRole());
                return map;
            }
    );

    @Override
    public void register(String username, String password) {
        PasswordService.hashPassword(password);
    }

    @Override
    public void login(String username, String password) {
        PasswordService.verifyPassword(password, "");
    }
}
