package whz.pti.services;

public interface AuthService {
    void register(String username, String password);
    void login(String username, String password);
}
