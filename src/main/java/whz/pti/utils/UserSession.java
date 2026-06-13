package whz.pti.utils;

import whz.pti.models.SafeUser;
import whz.pti.services.AuthService;

public class UserSession {
    private final static AuthService authService = AppContext.getInstance().getAuthService();

    private static Long currentUserId = -1L;
    private static SafeUser currentUser;

    public static void setSession(SafeUser user) {
        currentUser = user;
        if(user == null) {
            currentUserId = -1L;
        } else {
            currentUserId = user.getId();
        }
    }

    public static void clearSession() {
        currentUser = null;
        currentUserId = -1L;
    }

    public static SafeUser getCurrentUser() {
        return currentUser;
    }

    public static Long getCurrentUserId() {
        return currentUserId;
    }

    public static boolean isAuthenticated() {
        return currentUser != null;
    }
}
