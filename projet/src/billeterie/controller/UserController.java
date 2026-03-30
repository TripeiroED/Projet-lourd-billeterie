package billeterie.controller;

import billeterie.model.User;
import billeterie.model.UserDAO;
import billeterie.model.UserProfileData;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class UserController {
    private final UserDAO userDAO;

    public UserController(Connection conn) {
        this.userDAO = new UserDAO(conn);
    }

    public User authenticate(String username, String password) throws SQLException {
        return userDAO.authenticate(username, password);
    }

    public boolean register(String username, String password, String role, String fullname,
            String email, String phone, LocalDate birthdate, String address) throws SQLException {
        return userDAO.register(username, password, role, fullname, email, phone, birthdate, address);
    }

    public List<User> findAll() throws SQLException {
        return userDAO.findAll();
    }

    public boolean updateRole(String username, String newRole) throws SQLException {
        return userDAO.updateRole(username, newRole);
    }

    public boolean deleteUser(String username) throws SQLException {
        userDAO.delete(username);
        return true;
    }

    public UserProfileData findProfileByUsername(String username) throws SQLException {
        return userDAO.findProfileByUsername(username);
    }

    public boolean updateProfile(String username, String fullname, String email, String phone,
            LocalDate birthdate, String address) throws SQLException {
        return userDAO.updateProfile(username, fullname, email, phone, birthdate, address);
    }

    public void updateProfileImage(String username, String imagePath) throws SQLException {
        userDAO.updateProfileImage(username, imagePath);
    }
}
