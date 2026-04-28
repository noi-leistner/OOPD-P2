package Business.Entities;

public class User {
    String name;
    String surname;
    String email;
    String password;
    int id;
    String role;

    public User(String name, String surname, String email, String password, String role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(int id, String name, String surname, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
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
}
