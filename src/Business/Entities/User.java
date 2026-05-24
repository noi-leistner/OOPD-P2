package Business.Entities;

/**
 * Represents a person registered in the parking system.
 * <p>
 * This class stores a user's personal details (name, email), login credentials
 * (username, password), and their system role (such as "admin" or "client")
 * to handle permissions.
 */
public class User {

    /** The unique ID number for the user in the database. */
    private int id;

    /** The user's first name. */
    private final String name;

    /** The user's last name. */
    private final String surname;

    /** The user's email address. */
    private final String email;

    /** The username used for logging into the system. */
    private String username;

    /** The password used for logging into the system. */
    private String password;

    /** The system role assigned to the user (e.g., "admin", "client"). */
    private String role;

    /**
     * Creates a new User without a database ID (usually used during registration).
     *
     * @param name     the user's first name
     * @param surname  the user's last name
     * @param email    the user's email address
     * @param username the chosen username
     * @param password the chosen password
     * @param role     the assigned system permission role
     */
    public User(String name, String surname, String email, String username, String password, String role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    /**
     * Creates an existing User with a specific database ID (usually used when loading data).
     *
     * @param id       the unique database ID
     * @param name     the user's first name
     * @param surname  the user's last name
     * @param email    the user's email address
     * @param username the user's username
     * @param password the user's password
     * @param role     the user's system permission role
     */
    public User(int id, String name, String surname, String email, String username, String password, String role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    /**
     * Gets the unique ID of the user.
     *
     * @return the user ID
     */
    public int getId()          { return id; }

    /**
     * Gets the user's first name.
     *
     * @return the first name string
     */
    public String getName()     { return name; }

    /**
     * Gets the user's last name.
     *
     * @return the last name string
     */
    public String getSurname()  { return surname; }

    /**
     * Gets the user's email address.
     *
     * @return the email string
     */
    public String getEmail()    { return email; }

    /**
     * Gets the user's login password.
     *
     * @return the password string
     */
    public String getPassword() { return password; }

    /**
     * Gets the system role of the user.
     *
     * @return the role string
     */
    public String getRole()    { return role; }

    /**
     * Updates the user's system role.
     *
     * @param role the new role to set (e.g., "admin")
     */
    public void setRole(String role) { this.role = role; }

    /**
     * Updates the user's account password.
     *
     * @param password the new password string
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Gets the account username.
     *
     * @return the username string
     */
    public String getUsername() { return username; }

    /**
     * Checks if the user has administrative privileges.
     *
     * @return true if the role is "admin" (case-insensitive), false otherwise
     */
    public boolean isAdmin() {
        return this.role.equalsIgnoreCase("admin");
    }
}