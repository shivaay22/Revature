package com.revworkforce.dao;

import com.revworkforce.model.User;
import com.revworkforce.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User authenticate(String employeeId, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE employee_id = ? AND is_active = TRUE";

        System.out.println("Attempting login for: " + employeeId); // Debug output

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password_hash");
                System.out.println("Stored password: " + storedPassword); // Debug output
                System.out.println("Entered password: " + password); // Debug output


                if (com.revworkforce.utils.PasswordUtil.verifyPassword(password, storedPassword)) {
                    System.out.println("Password match successful!"); // Debug output
                    return mapResultSetToUser(rs);
                } else {
                    System.out.println("Password mismatch!"); // Debug output
                }
            } else {
                System.out.println("User not found!"); // Debug output
            }
        } catch (SQLException e) {
            System.err.println("Database error in authenticate: " + e.getMessage());
            throw e;
        }
        return null;
    }

    public User getUserById(int userId) throws SQLException {
        String query = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
        return null;
    }

    public User getUserByEmployeeId(String employeeId) throws SQLException {
        String query = "SELECT * FROM users WHERE employee_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
        return null;
    }

    public List<User> getAllEmployees() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role != 'ADMIN'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    public List<User> getEmployeesByManager(int managerId) throws SQLException {
        List<User> employees = new ArrayList<>();
        String query = "SELECT * FROM users WHERE manager_id = ? AND is_active = TRUE AND role != 'ADMIN'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, managerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                employees.add(mapResultSetToUser(rs));
            }
        }
        return employees;
    }

    public boolean createUser(User user) throws SQLException {
        String query = "INSERT INTO users (employee_id, full_name, email, password_hash, phone, " +
                "address, emergency_contact, date_of_birth, joining_date, department, " +
                "designation, role, manager_id, salary, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getEmployeeId());
            pstmt.setString(2, user.getFullName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPasswordHash());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getAddress());
            pstmt.setString(7, user.getEmergencyContact());
            pstmt.setDate(8, user.getDateOfBirth() != null ? new java.sql.Date(user.getDateOfBirth().getTime()) : null);
            pstmt.setDate(9, user.getJoiningDate() != null ? new java.sql.Date(user.getJoiningDate().getTime()) : null);
            pstmt.setString(10, user.getDepartment());
            pstmt.setString(11, user.getDesignation());
            pstmt.setString(12, user.getRole().toString());
            pstmt.setObject(13, user.getManagerId());
            pstmt.setDouble(14, user.getSalary() != null ? user.getSalary() : 0);
            pstmt.setBoolean(15, user.isActive());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateUser(User user) throws SQLException {
        String query = "UPDATE users SET full_name = ?, email = ?, phone = ?, address = ?, " +
                "emergency_contact = ?, department = ?, designation = ?, manager_id = ?, " +
                "salary = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getAddress());
            pstmt.setString(5, user.getEmergencyContact());
            pstmt.setString(6, user.getDepartment());
            pstmt.setString(7, user.getDesignation());
            pstmt.setObject(8, user.getManagerId());
            pstmt.setDouble(9, user.getSalary() != null ? user.getSalary() : 0);
            pstmt.setInt(10, user.getUserId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateProfile(int userId, String phone, String address, String emergencyContact) throws SQLException {
        String query = "UPDATE users SET phone = ?, address = ?, emergency_contact = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, phone);
            pstmt.setString(2, address);
            pstmt.setString(3, emergencyContact);
            pstmt.setInt(4, userId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean changePassword(int userId, String newPasswordHash) throws SQLException {
        String query = "UPDATE users SET password_hash = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newPasswordHash);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deactivateUser(int userId) throws SQLException {
        String query = "UPDATE users SET is_active = FALSE WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean activateUser(int userId) throws SQLException {
        String query = "UPDATE users SET is_active = TRUE WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<User> searchEmployees(String keyword) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role != 'ADMIN' AND " +
                "(employee_id LIKE ? OR full_name LIKE ? OR department LIKE ? OR designation LIKE ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmployeeId(rs.getString("employee_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setEmergencyContact(rs.getString("emergency_contact"));
        user.setDateOfBirth(rs.getDate("date_of_birth"));
        user.setJoiningDate(rs.getDate("joining_date"));
        user.setDepartment(rs.getString("department"));
        user.setDesignation(rs.getString("designation"));

        String roleStr = rs.getString("role");
        if (roleStr != null) {
            user.setRole(User.UserRole.valueOf(roleStr));
        }

        int managerId = rs.getInt("manager_id");
        user.setManagerId(managerId != 0 ? managerId : null);

        user.setSalary(rs.getDouble("salary"));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }
}