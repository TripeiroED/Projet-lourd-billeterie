package billeterie.model;

public class User {
    private final String username;
    private final String role;
    private final String fullname;
    private final String email;

    public User(String username, String role) {
        this(username, role, null, null);
    }

    public User(String username, String role, String fullname, String email) {
        this.username = username;
        this.role = role;
        this.fullname = fullname;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }
}
