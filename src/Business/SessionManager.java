package Business;

import Business.Entities.User;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;

    /**
     * Manages the authenticated user session (singleton)
     * --- Responsabilities: ---
     * - Hold the currently logged-in user across the application
     * - Provide login/logout state to controllers.
     */
    protected SessionManager() {}

    /**
     * Returns the singleton instance (lazy initialization).
     * @return the shared SessionManager instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(User user)  { this.currentUser = user; }
    public void logout()          { this.currentUser = null; }
    public User getCurrentUser()  { return currentUser; }
}
