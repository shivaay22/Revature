package com.revworkforce.controller;

import com.revworkforce.exceptions.ValidationException;
import com.revworkforce.model.User;
import com.revworkforce.service.UserService;
import com.revworkforce.utils.SessionManager;


import java.sql.SQLException;
import java.util.Scanner;

public class AuthController {
    private UserService userService;
    private Scanner scanner;

    public AuthController() {
        this.userService = new UserService();
        this.scanner = new Scanner(System.in);
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