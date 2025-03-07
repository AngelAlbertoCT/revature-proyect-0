package com.projecto_0.dao;
import com.projecto_0.config.DatabaseConfig;
import com.projecto_0.models.UserModel;
import com.projecto_0.service.AuthService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDao {

    //Insert new user
    public boolean registerUser(UserModel user) {
        AuthService authService = new AuthService();
        String hashedPassword = authService.hashPassword(user.getPassword());

        user.setPassword(hashedPassword);

        String query = "INSERT INTO users (name, last_name, email, password, role_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setInt(5, user.getRoleId());

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public UserModel getUserById(int id) {
        UserModel user = null;
        String query = "SELECT id, name, last_name, email, password, role_id FROM users WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new UserModel();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRoleId(rs.getInt("role_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public UserModel getUserByEmail(String email) {
        UserModel user = null;
        String query = "SELECT id, name, last_name, email, password, role_id FROM users WHERE email = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new UserModel();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRoleId(rs.getInt("role_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Show all users
    public List<UserModel> getAllUsers() {
        List<UserModel> users = new ArrayList<>();
        String query = "SELECT u.id, u.name, u.last_name, u.email, u.password, r.role AS role_name " +
                "FROM users u JOIN roles r ON u.role_id = r.id";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                UserModel user = new UserModel();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRoleName(rs.getString("role_name"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public boolean updateUserInfo(String email, String field, String newValue) {
        String query = "UPDATE users SET " + field + " = ? WHERE email = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, newValue);
            pstmt.setString(2, email);

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;  // Devuelve true si se actualizó al menos una fila
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, Object>> getAllUserInfo() {
        List<Map<String, Object>> userInfoList = new ArrayList<>();
        String query = "SELECT u.name, u.last_name, u.email, lt.type, l.amount, s.status " +
                "FROM users u " +
                "LEFT JOIN loans l ON u.id = l.user_id " +
                "LEFT JOIN loan_types lt ON l.loan_type_id = lt.id " +
                "LEFT JOIN statuses s ON l.status_id = s.id";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("name", rs.getString("name"));
                userInfo.put("last_name", rs.getString("last_name"));
                userInfo.put("email", rs.getString("email"));
                userInfo.put("loan_type", rs.getString("type"));
                userInfo.put("loan_amount", rs.getDouble("amount"));
                userInfo.put("loan_status", rs.getString("status"));
                userInfoList.add(userInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userInfoList;
    }

    public List<Map<String, Object>> getUserInfoById(int userId) {
        List<Map<String, Object>> userInfoList = new ArrayList<>();
        String query = "SELECT u.name, u.last_name, u.email, lt.type, l.amount, s.status " +
                "FROM users u " +
                "LEFT JOIN loans l ON u.id = l.user_id " +
                "LEFT JOIN loan_types lt ON l.loan_type_id = lt.id " +
                "LEFT JOIN statuses s ON l.status_id = s.id " +
                "WHERE u.id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("name", rs.getString("name"));
                userInfo.put("last_name", rs.getString("last_name"));
                userInfo.put("email", rs.getString("email"));
                userInfo.put("loan_type", rs.getString("type"));
                userInfo.put("loan_amount", rs.getDouble("amount"));
                userInfo.put("loan_status", rs.getString("status"));
                userInfoList.add(userInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userInfoList;
    }
}