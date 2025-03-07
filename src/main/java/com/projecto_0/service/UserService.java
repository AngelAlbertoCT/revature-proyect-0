package com.projecto_0.service;

import com.projecto_0.dao.UserDao;
import com.projecto_0.models.UserModel;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Map;

public class UserService {

    private UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Registers a new user if the email does not already exist.
     *
     * @param name      The user's name.
     * @param lastName  The user's last name.
     * @param email     The user's email.
     * @param password  The user's password.
     * @param roleId    The user's role ID.
     * @return The registered user, or null if the email already exists.
     */
    public UserModel registerUser(String name, String lastName, String email, String password, int roleId) {
        if (userDao.emailExists(email)) {
            return null; // Email already exists
        }

        UserModel newUser = new UserModel();
        newUser.setName(name);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPassword(BCrypt.hashpw(password, BCrypt.gensalt())); // Hash the password
        newUser.setRoleId(roleId);

        boolean success = userDao.registerUser(newUser);
        return success ? newUser : null;
    }

    /**
     * Logs in a user if the credentials are valid.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @return true if the credentials are valid, false otherwise.
     */
    public boolean loginUser(String email, String password) {
        UserModel user = userDao.getUserByEmail(email);
        if (user == null) {
            return false; // User not found
        }

        return BCrypt.checkpw(password, user.getPassword()); // Check if the password matches
    }

    /**
     * Updates user information.
     *
     * @param email    The user's email.
     * @param field    The field to update (e.g., "name", "last_name").
     * @param newValue The new value for the field.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateUserInfo(String email, String field, String newValue) {
        return userDao.updateUserInfo(email, field, newValue);
    }

    /**
     * Retrieves all users' information.
     *
     * @param userId The ID of the logged-in user.
     * @return A list of user information, or an empty list if the user is not authorized.
     */
    public List<Map<String, Object>> getUserInfo(int userId) {
        UserModel user = userDao.getUserById(userId);
        if (user == null || user.getRoleId() != 1) {
            return List.of(); // Only admins can see all users' information
        }

        return userDao.getAllUserInfo();
    }

    /**
     * Retrieves information for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of user information, or an empty list if the user is not found.
     */
    public List<Map<String, Object>> getUserInfoById(int userId) {
        return userDao.getUserInfoById(userId);
    }
}