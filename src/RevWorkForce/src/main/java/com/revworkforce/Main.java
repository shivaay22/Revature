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
                System.out.print("Do you want to login as another user? (yes/no): ");
                String continueChoice = scanner.nextLine();
                if (!continueChoice.equalsIgnoreCase("yes")) {
                    System.out.println("Thank you for using RevWorkForce. Goodbye!");
                    break;
                }
            }
        }

        scanner.close();
        DatabaseConnection.closeConnection();
    }
}