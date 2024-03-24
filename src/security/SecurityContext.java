package security;

import model.User;

public class SecurityContext {

    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void saveSecurityContext(User user) {
        currentUser = user;
    }
}
