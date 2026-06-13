package whz.pti.services;

public interface AuthService {
    void register(String username, String password);
    Long login(String username, String password);
}
