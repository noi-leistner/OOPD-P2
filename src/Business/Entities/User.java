package Business.Entities;

public class User {
    private int id;
    private String name;
    private String surname;
    private String email;
    private String username;
    private String password;
    private String role;

    public User(String name, String surname, String email, String username, String password, String role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(int id, String name, String surname, String email, String username, String password, String role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getId()          { return id; }
    public String getName()     { return name; }
    public String getSurname()  { return surname; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public String getRole()    { return role; }
    public void setRole(String role) { this.role = role; }
    public void setPassword(String password) { this.password = password; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public boolean isAdmin() {
        return this.role.equalsIgnoreCase("admin");
    }
}
