package com.projecto_0.dao;
import com.projecto_0.config.DatabaseConfig;
import com.projecto_0.models.LoanModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class LoanDao {
    public boolean createLoan(LoanModel loan) {
        String query = "INSERT INTO loans (user_id, loan_type_id, amount, status_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, loan.getUserId());
            pstmt.setInt(2, loan.getLoanTypeId());
            pstmt.setDouble(3, loan.getAmount());
            pstmt.setInt(4, loan.getStatusId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Object> getLoanById(int loanId) {
        Map<String, Object> loanDetails = null;
        String query = "SELECT l.id, l.user_id, l.loan_type_id, l.amount, l.status_id, " +
                "u.name AS user_name, u.last_name AS user_last_name, u.email AS user_email, " +
                "lt.type AS loan_type_name, s.status AS status_name " +
                "FROM loans l " +
                "JOIN users u ON l.user_id = u.id " +
                "JOIN loan_types lt ON l.loan_type_id = lt.id " +
                "JOIN statuses s ON l.status_id = s.id " +
                "WHERE l.id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, loanId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                loanDetails = new HashMap<>();
                loanDetails.put("id", rs.getInt("id"));
                loanDetails.put("user_id", rs.getInt("user_id"));
                loanDetails.put("user_name", rs.getString("user_name"));
                loanDetails.put("user_last_name", rs.getString("user_last_name"));
                loanDetails.put("user_email", rs.getString("user_email"));
                loanDetails.put("loan_type_id", rs.getInt("loan_type_id"));
                loanDetails.put("loan_type_name", rs.getString("loan_type_name"));
                loanDetails.put("amount", rs.getDouble("amount"));
                loanDetails.put("status_id", rs.getInt("status_id"));
                loanDetails.put("status_name", rs.getString("status_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loanDetails;
    }

    public boolean updateLoan(int loanId, int loanTypeId, double amount) {
        String query = "UPDATE loans SET loan_type_id = ?, amount = ? WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, loanTypeId);
            pstmt.setDouble(2, amount);
            pstmt.setInt(3, loanId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateLoanStatus(int loanId, int statusId) {
        String query = "UPDATE loans SET status_id = ? WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, statusId);
            pstmt.setInt(2, loanId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
