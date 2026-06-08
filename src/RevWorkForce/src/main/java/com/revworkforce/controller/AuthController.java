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

    public boolean register(String employeeId, String fullName, String email, String password,
                            User.UserRole role, String phone, String department, String designation) {
        try {
            User user = new User();
            user.setEmployeeId(employeeId);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPasswordHash(password);
            user.setRole(role);
            user.setPhone(phone);
            user.setDepartment(department);
            user.setDesignation(designation);
            user.setActive(true);
            user.setJoiningDate(new Date());

            return userService.createUser(user);
        } catch (Exception e) {
            if (e.getMessage() != null && (e.getMessage().contains("already exists") || e.getMessage().contains("Duplicate entry"))) {
                System.out.println("Error: Employee ID or Email already exists!");
            } else {
                System.out.println("Error: " + e.getMessage());
            }
            return false;
        }
    }

    public User login() {
        System.out.println("\n=== RevWorkForce Login ===");
        System.out.print("Employee ID: ");
        String employeeId = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            if (employeeId == null || employeeId.trim().isEmpty()) {
                throw new ValidationException("Username cannot be empty");
            }

            if (password == null || password.isEmpty()) {
                throw new ValidationException("Password cannot be empty");
            }

            User user = userService.login(employeeId, password);
            if (user != null) {
                SessionManager.getInstance().login(user);
                System.out.println("\n Login successful! Welcome, " + user.getFullName());
                return user;
            } else {
                System.out.println("\n Invalid credentials. Please try again.");
            }
        } catch (ValidationException e) {
            System.out.println("\n Validation error: " + e.getMessage());
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