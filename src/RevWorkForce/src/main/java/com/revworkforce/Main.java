package com.revworkforce;

import com.revworkforce.controller.AdminController;
import com.revworkforce.controller.AuthController;
import com.revworkforce.controller.EmployeeController;
import com.revworkforce.controller.ManagerController;
import com.revworkforce.model.User;
import com.revworkforce.utils.DatabaseConnection;

import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static AuthController authController = new AuthController();

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down RevWorkForce...");
            DatabaseConnection.closeConnection();
        }));

        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("=== Welcome to RevWorkForce ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    registerUser();
                    break;
                case "2":
                    User currentUser = authController.login();
                    if (currentUser != null) {
                        switch (currentUser.getRole()) {
                            case ADMIN:
                                AdminController adminController = new AdminController(currentUser);
                                adminController.showAdminMenu();
                                break;
                            case MANAGER:
                                ManagerController managerController = new ManagerController(currentUser);
                                managerController.showManagerMenu();
                                break;
                            case EMPLOYEE:
                                EmployeeController employeeController = new EmployeeController(currentUser);
                                employeeController.showMenu();
                                break;
                        }

                        System.out.println("\n" + "=".repeat(50));
                        System.out.print("Do you want to continue? (yes/no): ");
                        String continueChoice = scanner.nextLine();
                        if (!continueChoice.equalsIgnoreCase("yes")) {
                            System.out.println("Thank you for using RevWorkForce. Goodbye!");
                            scanner.close();
                            DatabaseConnection.closeConnection();
                            return;
                        }
                    }
                    break;
                case "3":
                    System.out.println("Thank you for using RevWorkForce. Goodbye!");
                    scanner.close();
                    DatabaseConnection.closeConnection();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void registerUser() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("=== New User Registration ===");

        System.out.print("Enter Employee ID: ");
        String employeeId = scanner.nextLine();

        System.out.print("Enter Full Name: ");
        String fullName = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        System.out.println("\nSelect Role:");
        System.out.println("1. ADMIN");
        System.out.println("2. MANAGER");
        System.out.println("3. EMPLOYEE");
        System.out.print("Enter role choice: ");

        String roleChoice = scanner.nextLine();
        User.UserRole role;

        switch (roleChoice) {
            case "1":
                role = User.UserRole.ADMIN;
                break;
            case "2":
                role = User.UserRole.MANAGER;
                break;
            case "3":
                role = User.UserRole.EMPLOYEE;
                break;
            default:
                System.out.println("Invalid role choice. Registration cancelled.");
                return;
        }
        System.out.print("Enter Phone (optional): ");
        String phone = scanner.nextLine();

        System.out.print("Enter Department (optional): ");
        String department = scanner.nextLine();

        System.out.print("Enter Designation (optional): ");
        String designation = scanner.nextLine();

        boolean success = authController.register(employeeId, fullName, email, password, role, phone, department, designation);

        if (success) {
            System.out.println("\n Registration successful! You can now login.");
        } else {
            System.out.println("\n Registration failed. Employee ID or Email might already exist.");
        }
    }
}