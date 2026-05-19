package com.revworkforce.controller;

import com.revworkforce.exceptions.ValidationException;
import com.revworkforce.model.User;
import com.revworkforce.service.UserService;
import com.revworkforce.utils.DatabaseConnection;
import com.revworkforce.utils.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;

public class AuthController {
    private UserService userService;
    private Scanner scanner;

    public AuthController() {
        this.userService = new UserService();
        this.scanner = new Scanner(System.in);
    }

    // Add this method to AuthController class
    public boolean register(String employeeId, String fullName, String email, String password,
                            User.UserRole role, String phone, String department, String designation) {
        String query = "INSERT INTO users (employee_id, full_name, email, password_hash, role, phone, department, designation, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Hash the password before storing (for security)
            String hashedPassword = hashPassword(password);

            pstmt.setString(1, employeeId);
            pstmt.setString(2, fullName);
            pstmt.setString(3, email);
            pstmt.setString(4, hashedPassword);
            pstmt.setString(5, role.name());
            pstmt.setString(6, phone != null && !phone.isEmpty() ? phone : null);
            pstmt.setString(7, department != null && !department.isEmpty() ? department : null);
            pstmt.setString(8, designation != null && !designation.isEmpty() ? designation : null);
            pstmt.setBoolean(9, true);
            pstmt.setTimestamp(10, new java.sql.Timestamp(new Date().getTime()));
            pstmt.setTimestamp(11, new java.sql.Timestamp(new Date().getTime()));

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println("Error: Employee ID or Email already exists!");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    // Helper method to hash password (you can use BCrypt or simple hashing)
    private String hashPassword(String password) {
        // For production, use BCrypt or a proper hashing algorithm
        // This is a simple example - use a proper hash in production!
        return password; // Replace with actual hashing like BCrypt
    }

    public User login() {
        System.out.println("\n=== RevWorkForce Login ===");
        System.out.print("Employee ID: ");
        String employeeId = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            User user = userService.login(employeeId, password);
            if (employeeId == null || employeeId.trim().isEmpty()) {
                throw new ValidationException("Username cannot be empty");
            }

            if (password == null || password.isEmpty()) {
                throw new ValidationException("Password cannot be empty");
            }
            if (user != null) {
                SessionManager.getInstance().login(user);
                System.out.println("\n Login successful! Welcome, " + user.getFullName());
                return user;
            } else {
                System.out.println("\n Invalid credentials. Please try again.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return null;
    }

    public void logout() {
        SessionManager.getInstance().logout();
        System.out.println("You have been logged out successfully.");
    }

    public boolean changePassword(User user) {
        System.out.println("\n=== Change Password ===");
        System.out.print("Current Password: ");
        String oldPassword = scanner.nextLine();
        System.out.print("New Password: ");
        String newPassword = scanner.nextLine();
        System.out.print("Confirm New Password: ");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println(" Passwords do not match!");
            return false;
        }

        if (newPassword.length() < 6) {
            System.out.println(" Password must be at least 6 characters long!");
            return false;
        }

        try {
            if (userService.changePassword(user.getUserId(), oldPassword, newPassword)) {
                System.out.println(" Password changed successfully!");
                return true;
            } else {
                System.out.println(" Current password is incorrect!");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
            return false;
        }
    }
}