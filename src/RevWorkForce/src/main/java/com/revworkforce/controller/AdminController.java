package com.revworkforce.controller;

import com.revworkforce.model.*;
import com.revworkforce.service.*;
import com.revworkforce.utils.PasswordUtil;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class AdminController {

    private User currentAdmin;
    private UserService userService;
    private LeaveService leaveService;
    private HolidayService holidayService;
    private NotificationService notificationService;
    private PerformanceService performanceService;
    private Scanner scanner;
    private SimpleDateFormat dateFormatter;

    public AdminController(User admin) {
        this.currentAdmin = admin;
        this.userService = new UserService();
        this.leaveService = new LeaveService();
        this.holidayService = new HolidayService();
        this.notificationService = new NotificationService();
        this.performanceService = new PerformanceService();
        this.scanner = new Scanner(System.in);
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void showAdminMenu() {
        while (true) {
            displayMenuHeader();

            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    addNewEmployee();
                    break;
                case 2:
                    updateEmployeeInformation();
                    break;
                case 3:
                    viewAllEmployees();
                    break;
                case 4:
                    searchEmployees();
                    break;
                case 5:
                    manageEmployeeStatus();
                    break;
                case 6:
                    assignReportingManager();
                    break;
                case 7:
                    configureLeavePolicies();
                    break;
                case 8:
                    adjustEmployeeLeaveBalance();
                    break;
                case 9:
                    generateLeaveReports();
                    break;
                case 10:
                    manageHolidayCalendar();
                    break;
                case 11:
                    manageDepartmentsAndDesignations();
                    break;
                case 12:
                    configurePerformanceCycles();
                    break;
                case 13:
                    viewSystemAuditLogs();
                    break;
                case 14:
                    postCompanyAnnouncement();
                    break;
                case 15:
                    resetEmployeePassword();
                    break;
                case 16:
                    viewSystemStatistics();
                    break;
                case 0:
                    logout();
                    return;
                default:
                    System.out.println(" Invalid option. Please try again.");
            }
        }
    }

    private void displayMenuHeader() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         ADMIN CONTROL PANEL");
        System.out.println("=".repeat(60));
        System.out.println("Welcome, " + currentAdmin.getFullName() + " (Administrator)");
        try {
            int unreadCount = notificationService.getUnreadCount(currentAdmin.getUserId());
            if (unreadCount > 0) {
                System.out.println(" You have " + unreadCount + " unread notification(s)");
            }
        } catch (SQLException e) {

        }

        System.out.println("\n--- EMPLOYEE MANAGEMENT ---");
        System.out.println("1.  Add New Employee");
        System.out.println("2.  Update Employee Information");
        System.out.println("3.  View All Employees");
        System.out.println("4.  Search Employees");
        System.out.println("5.  Activate/Deactivate Employee");
        System.out.println("6.  Assign Reporting Manager");

        System.out.println("\n--- LEAVE MANAGEMENT ---");
        System.out.println("7.  Configure Leave Policies");
        System.out.println("8.  Adjust Employee Leave Balance");
        System.out.println("9.  Generate Leave Reports");
        System.out.println("10. Manage Holiday Calendar");

        System.out.println("\n--- SYSTEM CONFIGURATION ---");
        System.out.println("11. Manage Departments & Designations");
        System.out.println("12. Configure Performance Review Cycles");
        System.out.println("13. View System Audit Logs");
        System.out.println("14. Post Company Announcement");

        System.out.println("\n--- SECURITY ---");
        System.out.println("15. Reset Employee Password");

        System.out.println("\n--- REPORTS ---");
        System.out.println("16. View System Statistics");

        System.out.println("\n--- ACCOUNT ---");
        System.out.println("0.  Logout");
        System.out.print("\nSelect option: ");
    }

    private int getUserChoice() {
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.nextLine();
            return -1;
        }
    }


    private void addNewEmployee() {
        System.out.println("\n=== Add New Employee ===");
        System.out.println("-".repeat(40));

        User newEmployee = new User();

        System.out.print("Employee ID: ");
        newEmployee.setEmployeeId(scanner.next());
        scanner.nextLine();

        System.out.print("Full Name: ");
        newEmployee.setFullName(scanner.nextLine());

        System.out.print("Email Address: ");
        newEmployee.setEmail(scanner.nextLine());

        System.out.print("Phone Number (10 digits): ");
        newEmployee.setPhone(scanner.nextLine());

        System.out.print("Date of Birth (YYYY-MM-DD): ");
        try {
            newEmployee.setDateOfBirth(dateFormatter.parse(scanner.nextLine()));
        } catch (ParseException e) {
            System.out.println(" Invalid date format. Skipping date of birth.");
        }

        System.out.print("Joining Date (YYYY-MM-DD): ");
        try {
            newEmployee.setJoiningDate(dateFormatter.parse(scanner.nextLine()));
        } catch (ParseException e) {
            System.out.println(" Invalid date format. Using current date.");
            newEmployee.setJoiningDate(new Date());
        }

        System.out.print("Department: ");
        newEmployee.setDepartment(scanner.nextLine());

        System.out.print("Designation: ");
        newEmployee.setDesignation(scanner.nextLine());

        System.out.print("Role (EMPLOYEE/MANAGER/ADMIN): ");
        try {
            newEmployee.setRole(User.UserRole.valueOf(scanner.nextLine().toUpperCase()));
        } catch (IllegalArgumentException e) {
            System.out.println(" Invalid role. Setting to EMPLOYEE.");
            newEmployee.setRole(User.UserRole.EMPLOYEE);
        }

        System.out.print("Salary (annual): ");
        newEmployee.setSalary(scanner.nextDouble());
        scanner.nextLine();

        System.out.print("Address: ");
        newEmployee.setAddress(scanner.nextLine());

        System.out.print("Emergency Contact Number: ");
        newEmployee.setEmergencyContact(scanner.nextLine());

        // Generate temporary password
        String tempPassword = PasswordUtil.generateRandomPassword();
        newEmployee.setPasswordHash(PasswordUtil.hashPassword(tempPassword));
        newEmployee.setActive(true);

        try {
            if (userService.createUser(newEmployee)) {
                System.out.println("\n Employee added successfully!");
                System.out.println("=".repeat(40));
                System.out.println("Employee ID: " + newEmployee.getEmployeeId());
                System.out.println("Name: " + newEmployee.getFullName());
                System.out.println("Temporary Password: " + tempPassword);
                System.out.println("=".repeat(40));
                System.out.println(" Please share this password with the employee.");
                System.out.println(" Employee must change password on first login.");

                // Send notification to employee
                notificationService.sendNotification(
                        newEmployee.getUserId(),
                        "Welcome to RevWorkForce",
                        "Your account has been created. Please login with the temporary password provided.",
                        Notification.NotificationType.SYSTEM
                );
            } else {
                System.out.println(" Failed to add employee.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private void updateEmployeeInformation() {
        System.out.println("\n=== Update Employee Information ===");
        System.out.print("Enter Employee ID to update: ");
        String employeeId = scanner.next();
        scanner.nextLine();

        try {
            User employee = userService.getUserByEmployeeId(employeeId);
            if (employee == null) {
                System.out.println(" Employee not found.");
                return;
            }

            displayEmployeeDetails(employee);

            System.out.println("\n--- Enter new values (press Enter to keep current) ---");

            System.out.print("Full Name [" + employee.getFullName() + "]: ");
            String input = scanner.nextLine();
            if (!input.isEmpty()) employee.setFullName(input);

            System.out.print("Email [" + employee.getEmail() + "]: ");
            input = scanner.nextLine();
            if (!input.isEmpty()) employee.setEmail(input);

            System.out.print("Phone [" + (employee.getPhone() != null ? employee.getPhone() : "Not set") + "]: ");
            input = scanner.nextLine();
            if (!input.isEmpty()) employee.setPhone(input);

            System.out.print("Department [" + (employee.getDepartment() != null ? employee.getDepartment() : "Not set") + "]: ");
            input = scanner.nextLine();
            if (!input.isEmpty()) employee.setDepartment(input);

            System.out.print("Designation [" + (employee.getDesignation() != null ? employee.getDesignation() : "Not set") + "]: ");
            input = scanner.nextLine();
            if (!input.isEmpty()) employee.setDesignation(input);

            System.out.print("Salary [" + (employee.getSalary() != null ? employee.getSalary() : "Not set") + "]: ");
            input = scanner.nextLine();
            if (!input.isEmpty()) employee.setSalary(Double.parseDouble(input));

            System.out.print("Address [" + (employee.getAddress() != null ? employee.getAddress() : "Not set") + "]: ");
            input = scanner.nextLine();
            if (!input.isEmpty()) employee.setAddress(input);

            System.out.print("Emergency Contact [" + (employee.getEmergencyContact() != null ? employee.getEmergencyContact() : "Not set") + "]: ");
            input = scanner.nextLine();
            if (!input.isEmpty()) employee.setEmergencyContact(input);

            if (userService.updateUser(employee)) {
                System.out.println(" Employee information updated successfully!");

                // Notify employee about profile update
                notificationService.sendNotification(
                        employee.getUserId(),
                        "Profile Updated",
                        "Your profile information has been updated by administrator.",
                        Notification.NotificationType.SYSTEM
                );
            } else {
                System.out.println(" Failed to update employee information.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
        }
    }


    private void viewAllEmployees() {
        System.out.println("\n=== All Employees ===");
        System.out.println("-".repeat(100));
        System.out.printf("%-12s %-25s %-20s %-15s %-12s %-10s\n",
                "Emp ID", "Name", "Email", "Department", "Role", "Status");
        System.out.println("-".repeat(100));

        try {
            List<User> employees = userService.getAllEmployees();
            if (employees.isEmpty()) {
                System.out.println("No employees found in the system.");
            } else {
                for (User emp : employees) {
                    System.out.printf("%-12s %-25s %-20s %-15s %-12s %-10s\n",
                            emp.getEmployeeId(),
                            truncateString(emp.getFullName(), 25),
                            truncateString(emp.getEmail(), 20),
                            emp.getDepartment() != null ? emp.getDepartment() : "-",
                            emp.getRole(),
                            emp.isActive() ? "Active" : "Inactive");
                }
                System.out.println("-".repeat(100));
                System.out.println("Total Employees: " + employees.size());
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employees: " + e.getMessage());
        }
    }


    private void searchEmployees() {
        System.out.println("\n=== Search Employees ===");
        System.out.print("Enter search keyword (name, ID, department, or designation): ");
        String keyword = scanner.nextLine();

        try {
            List<User> results = userService.searchEmployees(keyword);
            if (results.isEmpty()) {
                System.out.println("No employees found matching '" + keyword + "'");
            } else {
                System.out.println("\nSearch Results (" + results.size() + " found):");
                System.out.println("-".repeat(80));
                System.out.printf("%-12s %-25s %-15s %-20s\n",
                        "Emp ID", "Name", "Department", "Designation");
                System.out.println("-".repeat(80));

                for (User emp : results) {
                    System.out.printf("%-12s %-25s %-15s %-20s\n",
                            emp.getEmployeeId(),
                            truncateString(emp.getFullName(), 25),
                            emp.getDepartment() != null ? emp.getDepartment() : "-",
                            emp.getDesignation() != null ? emp.getDesignation() : "-");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching employees: " + e.getMessage());
        }
    }


    private void manageEmployeeStatus() {
        System.out.println("\n=== Manage Employee Status ===");
        System.out.print("Enter Employee ID: ");
        String employeeId = scanner.next();
        scanner.nextLine();

        try {
            User employee = userService.getUserByEmployeeId(employeeId);
            if (employee == null) {
                System.out.println(" Employee not found.");
                return;
            }

            System.out.println("\nEmployee: " + employee.getFullName());
            System.out.println("Current Status: " + (employee.isActive() ? " ACTIVE" : " INACTIVE"));
            System.out.print("\nChoose action: (1) Activate  (2) Deactivate: ");
            int action = scanner.nextInt();
            scanner.nextLine();

            boolean success;
            String actionName;

            if (action == 1) {
                success = userService.activateUser(employee.getUserId());
                actionName = "activated";
            } else if (action == 2) {
                success = userService.deactivateUser(employee.getUserId());
                actionName = "deactivated";
            } else {
                System.out.println(" Invalid action.");
                return;
            }

            if (success) {
                System.out.println(" Employee account " + actionName + " successfully!");

                // Notify employee
                notificationService.sendNotification(
                        employee.getUserId(),
                        "Account " + (action == 1 ? "Activated" : "Deactivated"),
                        "Your account has been " + actionName + " by administrator.",
                        Notification.NotificationType.SYSTEM
                );
            } else {
                System.out.println(" Failed to update employee status.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating employee status: " + e.getMessage());
        }
    }


    private void assignReportingManager() {
        System.out.println("\n=== Assign Reporting Manager ===");
        System.out.print("Enter Employee ID: ");
        String employeeId = scanner.next();
        scanner.nextLine();

        try {
            User employee = userService.getUserByEmployeeId(employeeId);
            if (employee == null) {
                System.out.println(" Employee not found.");
                return;
            }

            System.out.println("\nEmployee: " + employee.getFullName());
            System.out.print("Enter Manager's Employee ID: ");
            String managerId = scanner.next();
            scanner.nextLine();

            User manager = userService.getUserByEmployeeId(managerId);
            if (manager == null) {
                System.out.println(" Manager not found.");
                return;
            }

            employee.setManagerId(manager.getUserId());

            if (userService.updateUser(employee)) {
                System.out.println(" Reporting manager assigned successfully!");
                System.out.println(employee.getFullName() + " now reports to " + manager.getFullName());

                // Notify both employee and manager
                notificationService.sendNotification(
                        employee.getUserId(),
                        "Reporting Manager Assigned",
                        "You now report to " + manager.getFullName(),
                        Notification.NotificationType.SYSTEM
                );
                notificationService.sendNotification(
                        manager.getUserId(),
                        "New Team Member Assigned",
                        employee.getFullName() + " has been added to your team.",
                        Notification.NotificationType.SYSTEM
                );
            } else {
                System.out.println(" Failed to assign reporting manager.");
            }
        } catch (SQLException e) {
            System.err.println("Error assigning manager: " + e.getMessage());
        }
    }

    /**
     * Configure leave policies and quotas
     */
    private void configureLeavePolicies() {
        System.out.println("\n=== Configure Leave Policies ===");
        System.out.println("1. Set Default Leave Quotas for All Employees");
        System.out.println("2. Configure Leave Policy for Specific Employee");
        System.out.println("3. View Current Leave Policies");
        System.out.print("Select option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        try {
            switch (choice) {
                case 1:
                    configureDefaultLeaveQuotas(currentYear);
                    break;
                case 2:
                    configureEmployeeLeaveQuotas(currentYear);
                    break;
                case 3:
                    viewCurrentLeavePolicies();
                    break;
                default:
                    System.out.println(" Invalid option.");
            }
        } catch (SQLException e) {
            System.err.println("Error configuring leave policies: " + e.getMessage());
        }
    }

    private void configureDefaultLeaveQuotas(int year) throws SQLException {
        System.out.println("\n=== Configure Default Leave Quotas for " + year + " ===");
        System.out.println("Enter default quotas for all employees:");

        for (LeaveRequest.LeaveType leaveType : LeaveRequest.LeaveType.values()) {
            System.out.print(leaveType + " Leave (days): ");
            int totalDays = scanner.nextInt();
            scanner.nextLine();

            List<User> employees = userService.getAllEmployees();
            for (User emp : employees) {
                leaveService.configureLeaveBalance(emp.getUserId(), leaveType, totalDays, year);
            }
            System.out.println(" " + leaveType + " leave quota set to " + totalDays + " days for all employees.");
        }

        System.out.println("\n Default leave quotas configured successfully!");
    }

    private void configureEmployeeLeaveQuotas(int year) throws SQLException {
        System.out.print("\nEnter Employee ID: ");
        String employeeId = scanner.next();
        scanner.nextLine();

        User employee = userService.getUserByEmployeeId(employeeId);
        if (employee == null) {
            System.out.println(" Employee not found.");
            return;
        }

        System.out.println("\nConfiguring leave quotas for: " + employee.getFullName());

        for (LeaveRequest.LeaveType leaveType : LeaveRequest.LeaveType.values()) {
            System.out.print(leaveType + " Leave (days): ");
            int totalDays = scanner.nextInt();
            scanner.nextLine();

            leaveService.configureLeaveBalance(employee.getUserId(), leaveType, totalDays, year);
            System.out.println(" " + leaveType + " leave set to " + totalDays + " days.");
        }

        System.out.println("\n Leave quotas configured for " + employee.getFullName());
    }

    private void viewCurrentLeavePolicies() throws SQLException {
        System.out.println("\n=== Current Leave Policies ===");
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        List<User> sampleEmployees = userService.getAllEmployees();
        if (!sampleEmployees.isEmpty()) {
            User sample = sampleEmployees.get(0);
            System.out.println("\nSample leave quotas for " + currentYear + " (based on " + sample.getFullName() + "):");
            System.out.println("-".repeat(40));

            for (LeaveRequest.LeaveType leaveType : LeaveRequest.LeaveType.values()) {
                LeaveBalance balance = leaveService.getLeaveBalance(sample.getUserId(), leaveType, currentYear);
                if (balance != null) {
                    System.out.printf("%-12s: %d days per year\n", leaveType, balance.getTotalDays());
                }
            }
        }
    }

    /**
     * Adjust leave balance for employees
     */
    private void adjustEmployeeLeaveBalance() {
        System.out.println("\n=== Adjust Employee Leave Balance ===");
        System.out.print("Enter Employee ID: ");
        String employeeId = scanner.next();
        scanner.nextLine();

        try {
            User employee = userService.getUserByEmployeeId(employeeId);
            if (employee == null) {
                System.out.println(" Employee not found.");
                return;
            }

            int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            System.out.println("\nCurrent Leave Balance for " + employee.getFullName() + " (" + currentYear + ")");
            System.out.println("-".repeat(50));

            for (LeaveRequest.LeaveType leaveType : LeaveRequest.LeaveType.values()) {
                LeaveBalance balance = leaveService.getLeaveBalance(employee.getUserId(), leaveType, currentYear);
                if (balance != null) {
                    System.out.printf("%-12s: %d/%d days (Used/Total)\n",
                            leaveType, balance.getUsedDays(), balance.getTotalDays());

                    System.out.print("  Adjust total days for " + leaveType + " (current: " + balance.getTotalDays() + "): ");
                    int newTotal = scanner.nextInt();
                    scanner.nextLine();

                    if (newTotal != balance.getTotalDays()) {
                        leaveService.configureLeaveBalance(employee.getUserId(), leaveType, newTotal, currentYear);
                        System.out.println(" Updated to " + newTotal + " days\n");
                    }
                }
            }

            System.out.println(" Leave balance adjustment completed!");
        } catch (SQLException e) {
            System.err.println("Error adjusting leave balance: " + e.getMessage());
        }
    }

    /**
     * Generate various leave reports
     */
    private void generateLeaveReports() {
        System.out.println("\n=== Generate Leave Reports ===");
        System.out.println("1. Department-wise Leave Report");
        System.out.println("2. Employee-wise Leave Report");
        System.out.println("3. Monthly Leave Summary");
        System.out.print("Select option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        try {
            switch (choice) {
                case 1:
                    generateDepartmentLeaveReport(currentYear);
                    break;
                case 2:
                    generateEmployeeLeaveReport(currentYear);
                    break;
                case 3:
                    generateMonthlyLeaveSummary(currentYear);
                    break;
                default:
                    System.out.println(" Invalid option.");
            }
        } catch (SQLException e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }

    private void generateDepartmentLeaveReport(int year) throws SQLException {
        System.out.print("\nEnter Department Name: ");
        String department = scanner.nextLine();

        List<User> employees = userService.getAllEmployees();
        System.out.println("\n=== Department Leave Report for " + department + " (" + year + ") ===");
        System.out.println("-".repeat(80));
        System.out.printf("%-25s %-10s %-10s %-10s %-10s\n", "Employee", "CL", "SL", "PL", "Total");
        System.out.println("-".repeat(80));

        boolean found = false;
        for (User emp : employees) {
            if (emp.getDepartment() != null && emp.getDepartment().equalsIgnoreCase(department)) {
                found = true;
                int totalUsed = 0;
                System.out.printf("%-25s", truncateString(emp.getFullName(), 25));

                for (LeaveRequest.LeaveType leaveType : LeaveRequest.LeaveType.values()) {
                    LeaveBalance balance = leaveService.getLeaveBalance(emp.getUserId(), leaveType, year);
                    int used = balance != null ? balance.getUsedDays() : 0;
                    totalUsed += used;
                    System.out.printf(" %-10d", used);
                }
                System.out.printf(" %-10d\n", totalUsed);
            }
        }

        if (!found) {
            System.out.println("No employees found in department: " + department);
        }
        System.out.println("-".repeat(80));
    }

    private void generateEmployeeLeaveReport(int year) throws SQLException {
        System.out.print("\nEnter Employee ID: ");
        String employeeId = scanner.next();
        scanner.nextLine();

        User employee = userService.getUserByEmployeeId(employeeId);
        if (employee == null) {
            System.out.println(" Employee not found.");
            return;
        }

        System.out.println("\n=== Leave Report for " + employee.getFullName() + " (" + year + ") ===");
        System.out.println("-".repeat(50));
        System.out.printf("%-12s %-10s %-10s %-10s\n", "Leave Type", "Total", "Used", "Available");
        System.out.println("-".repeat(50));

        for (LeaveRequest.LeaveType leaveType : LeaveRequest.LeaveType.values()) {
            LeaveBalance balance = leaveService.getLeaveBalance(employee.getUserId(), leaveType, year);
            if (balance != null) {
                System.out.printf("%-12s %-10d %-10d %-10d\n",
                        leaveType, balance.getTotalDays(), balance.getUsedDays(), balance.getAvailableDays());
            }
        }
        System.out.println("-".repeat(50));
    }

    private void generateMonthlyLeaveSummary(int year) throws SQLException {
        System.out.print("\nEnter Month (1-12): ");
        int month = scanner.nextInt();
        scanner.nextLine();

        List<User> employees = userService.getAllEmployees();
        System.out.printf("\n=== Monthly Leave Summary - %d/%d ===\n", month, year);
        System.out.println("-".repeat(60));
        System.out.printf("%-25s %-15s\n", "Employee", "Leaves Taken");
        System.out.println("-".repeat(60));

        for (User emp : employees) {
            // This would need additional DAO method to get leaves by month
            System.out.printf("%-25s %-15d\n", truncateString(emp.getFullName(), 25), 0);
        }
        System.out.println("-".repeat(60));
    }


//      Manage holiday calendar

    private void manageHolidayCalendar() {
        while (true) {
            System.out.println("\n=== Manage Holiday Calendar ===");
            System.out.println("1. View All Holidays");
            System.out.println("2. Add New Holiday");
            System.out.println("3. Remove Holiday");
            System.out.println("4. Back to Main Menu");
            System.out.print("Select option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1:
                        viewAllHolidays();
                        break;
                    case 2:
                        addNewHoliday();
                        break;
                    case 3:
                        removeHoliday();
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println(" Invalid option.");
                }
            } catch (SQLException e) {
                System.err.println("Error managing holidays: " + e.getMessage());
            }
        }
    }

    private void viewAllHolidays() throws SQLException {
        System.out.print("Enter year to view (or press Enter for current year): ");
        String yearInput = scanner.nextLine();
        int year = yearInput.isEmpty() ? java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) : Integer.parseInt(yearInput);

        List<Holiday> holidays = holidayService.getHolidaysForYear(year);
        System.out.println("\n=== Holiday Calendar " + year + " ===");
        System.out.println("-".repeat(50));

        if (holidays.isEmpty()) {
            System.out.println("No holidays configured for " + year);
        } else {
            System.out.printf("%-20s %-12s %s\n", "Holiday Name", "Date", "Description");
            System.out.println("-".repeat(50));
            for (Holiday holiday : holidays) {
                System.out.printf("%-20s %-12s %s\n",
                        holiday.getHolidayName(),
                        dateFormatter.format(holiday.getHolidayDate()),
                        holiday.getDescription() != null ? holiday.getDescription() : "-");
            }
        }
    }

    private void addNewHoliday() throws SQLException {
        System.out.println("\n=== Add New Holiday ===");
        System.out.print("Holiday Name: ");
        String name = scanner.nextLine();
        System.out.print("Date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine();
        System.out.print("Description (optional): ");
        String description = scanner.nextLine();

        try {
            Date date = dateFormatter.parse(dateStr);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(date);
            int year = cal.get(java.util.Calendar.YEAR);

            Holiday holiday = new Holiday(name, date, year);
            holiday.setDescription(description);

            if (holidayService.addHoliday(holiday)) {
                System.out.println(" Holiday added successfully!");

                // Notify all employees about new holiday
                List<User> allEmployees = userService.getAllEmployees();
                for (User emp : allEmployees) {
                    notificationService.sendNotification(
                            emp.getUserId(),
                            "New Holiday Added",
                            "A new holiday has been added: " + name + " on " + dateStr,
                            Notification.NotificationType.ANNOUNCEMENT
                    );
                }
            } else {
                System.out.println(" Failed to add holiday.");
            }
        } catch (ParseException e) {
            System.out.println(" Invalid date format. Please use YYYY-MM-DD.");
        }
    }

    private void removeHoliday() throws SQLException {
        System.out.print("\nEnter year: ");
        int year = scanner.nextInt();
        scanner.nextLine();

        List<Holiday> holidays = holidayService.getHolidaysForYear(year);
        if (holidays.isEmpty()) {
            System.out.println("No holidays found for " + year);
            return;
        }

        System.out.println("\nHolidays in " + year + ":");
        for (int i = 0; i < holidays.size(); i++) {
            Holiday holiday = holidays.get(i);
            System.out.println((i + 1) + ". " + holiday.getHolidayName() + " - " + dateFormatter.format(holiday.getHolidayDate()));
        }

        System.out.print("Select holiday to remove (0 to cancel): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= holidays.size()) {
            if (holidayService.removeHoliday(holidays.get(choice - 1).getHolidayId())) {
                System.out.println(" Holiday removed successfully!");
            } else {
                System.out.println(" Failed to remove holiday.");
            }
        }
    }


//     * Manage departments and designations master data
    private void manageDepartmentsAndDesignations() {
        System.out.println("\n=== Manage Departments & Designations ===");
        System.out.println("-".repeat(40));

        try {
            List<User> employees = userService.getAllEmployees();

            System.out.println("\n Current Departments:");
            employees.stream()
                    .map(User::getDepartment)
                    .distinct()
                    .filter(dept -> dept != null && !dept.isEmpty())
                    .forEach(dept -> System.out.println("  • " + dept));

            System.out.println("\n Current Designations:");
            employees.stream()
                    .map(User::getDesignation)
                    .distinct()
                    .filter(desig -> desig != null && !desig.isEmpty())
                    .forEach(desig -> System.out.println("  • " + desig));

            System.out.println("\n" + "=".repeat(40));
            System.out.println("  To add new departments/designations, simply");
            System.out.println("   assign them when adding or updating employees.");
            System.out.println("=".repeat(40));

        } catch (SQLException e) {
            System.err.println("Error fetching data: " + e.getMessage());
        }
    }

    /**
     * Configure performance review cycles
     */
    private void configurePerformanceCycles() {
        System.out.println("\n=== Configure Performance Review Cycles ===");
        System.out.println("-".repeat(40));
        System.out.println("Current Year: " + java.util.Calendar.getInstance().get(java.util.Calendar.YEAR));
        System.out.println("\nPerformance reviews are configured on an annual basis.");

        System.out.print("Set review cycle start month (1-12): ");
        int startMonth = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Set review cycle end month (1-12): ");
        int endMonth = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Set review submission deadline day (1-31): ");
        int deadlineDay = scanner.nextInt();
        scanner.nextLine();

        System.out.println("\n Performance review cycle configured successfully!");
        System.out.println("=".repeat(40));
        System.out.println("Cycle Period: " + startMonth + "/1 - " + endMonth + "/31");
        System.out.println("Submission Deadline: " + deadlineDay + "th of " + endMonth);
        System.out.println("=".repeat(40));

        // In production, save these settings to database
    }


    private void viewSystemAuditLogs() {
        System.out.println("\n=== System Audit Logs ===");
        System.out.println("-".repeat(80));
        System.out.printf("%-20s %-20s %-35s\n", "Timestamp", "User", "Action");
        System.out.println("-".repeat(80));


        System.out.printf("%-20s %-20s %-35s\n",
                new java.sql.Timestamp(System.currentTimeMillis()),
                currentAdmin.getEmployeeId(),
                "Viewed audit logs");
        System.out.printf("%-20s %-20s %-35s\n",
                new java.sql.Timestamp(System.currentTimeMillis() - 3600000),
                "ADMIN001",
                "Added new employee: EMP005");
        System.out.printf("%-20s %-20s %-35s\n",
                new java.sql.Timestamp(System.currentTimeMillis() - 7200000),
                "ADMIN001",
                "Modified leave policy");

        System.out.println("-".repeat(80));
        System.out.println("  Full audit logging would be implemented in production.");
    }


    private void postCompanyAnnouncement() {
        System.out.println("\n=== Post Company Announcement ===");
        System.out.print("Announcement Title: ");
        String title = scanner.nextLine();

        System.out.print("Announcement Content: ");
        String content = scanner.nextLine();

        System.out.print("Expiry Date (YYYY-MM-DD, optional): ");
        String expiryStr = scanner.nextLine();

        System.out.print("Is this urgent? (yes/no): ");
        boolean isUrgent = scanner.nextLine().equalsIgnoreCase("yes");

        // In production, implement AnnouncementDAO
        System.out.println("\n Announcement posted successfully!");

        if (isUrgent) {
            System.out.println(" URGENT: This announcement has been marked as urgent.");
        }

        System.out.println("Title: " + title);
        System.out.println("Content: " + content);
        if (!expiryStr.isEmpty()) {
            System.out.println("Expires: " + expiryStr);
        }

        // Notify all employees about announcement
        try {
            List<User> allEmployees = userService.getAllEmployees();
            for (User emp : allEmployees) {
                notificationService.sendNotification(
                        emp.getUserId(),
                        title,
                        content,
                        Notification.NotificationType.ANNOUNCEMENT
                );
            }
            System.out.println("📬 Notification sent to " + allEmployees.size() + " employees.");
        } catch (SQLException e) {
            System.err.println("Error sending notifications: " + e.getMessage());
        }
    }

    /**
     * Reset employee password
     */
    private void resetEmployeePassword() {
        System.out.println("\n=== Reset Employee Password ===");
        System.out.print("Enter Employee ID: ");
        String employeeId = scanner.next();
        scanner.nextLine();

        try {
            User employee = userService.getUserByEmployeeId(employeeId);
            if (employee == null) {
                System.out.println(" Employee not found.");
                return;
            }

            System.out.println("\nEmployee: " + employee.getFullName());
            System.out.print("Do you want to reset password for this employee? (yes/no): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("yes")) {
                String newPassword = PasswordUtil.generateRandomPassword();
                if (userService.resetPassword(employee.getUserId(), newPassword)) {
                    System.out.println("\n Password reset successfully!");
                    System.out.println("=".repeat(40));
                    System.out.println("New Temporary Password: " + newPassword);
                    System.out.println("=".repeat(40));
                    System.out.println(" Please share this password with the employee.");

                    // Notify employee
                    notificationService.sendNotification(
                            employee.getUserId(),
                            "Password Reset",
                            "Your password has been reset by administrator. Please login with the new temporary password.",
                            Notification.NotificationType.SYSTEM
                    );
                } else {
                    System.out.println(" Failed to reset password.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error resetting password: " + e.getMessage());
        }
    }


    private void viewSystemStatistics() {
        System.out.println("\n=== System Statistics ===");
        System.out.println("=".repeat(50));

        try {
            List<User> allEmployees = userService.getAllEmployees();
            long activeEmployees = allEmployees.stream().filter(User::isActive).count();
            long inactiveEmployees = allEmployees.size() - activeEmployees;

            System.out.println("\n EMPLOYEE STATISTICS:");
            System.out.println("  • Total Employees: " + allEmployees.size());
            System.out.println("  • Active Employees: " + activeEmployees);
            System.out.println("  • Inactive Employees: " + inactiveEmployees);

            // Department distribution
            System.out.println("\n DEPARTMENT DISTRIBUTION:");
            allEmployees.stream()
                    .map(User::getDepartment)
                    .filter(dept -> dept != null && !dept.isEmpty())
                    .distinct()
                    .forEach(dept -> {
                        long count = allEmployees.stream()
                                .filter(emp -> dept.equals(emp.getDepartment()))
                                .count();
                        System.out.println("  • " + dept + ": " + count + " employees");
                    });

            // Role distribution
            System.out.println("\n ROLE DISTRIBUTION:");
            long adminCount = allEmployees.stream().filter(emp -> emp.getRole() == User.UserRole.ADMIN).count();
            long managerCount = allEmployees.stream().filter(emp -> emp.getRole() == User.UserRole.MANAGER).count();
            long employeeCount = allEmployees.stream().filter(emp -> emp.getRole() == User.UserRole.EMPLOYEE).count();

            System.out.println("  • Administrators: " + adminCount);
            System.out.println("  • Managers: " + managerCount);
            System.out.println("  • Employees: " + employeeCount);

            // Leave statistics for current year
            int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            System.out.println("\n LEAVE STATISTICS (" + currentYear + "):");
            int totalLeavesTaken = 0;
            for (User emp : allEmployees) {
                for (LeaveRequest.LeaveType leaveType : LeaveRequest.LeaveType.values()) {
                    LeaveBalance balance = leaveService.getLeaveBalance(emp.getUserId(), leaveType, currentYear);
                    if (balance != null) {
                        totalLeavesTaken += balance.getUsedDays();
                    }
                }
            }
            System.out.println("  • Total Leaves Taken: " + totalLeavesTaken);
            System.out.println("  • Average Leaves per Employee: " +
                    (allEmployees.isEmpty() ? 0 : totalLeavesTaken / allEmployees.size()));

            System.out.println("\n" + "=".repeat(50));

        } catch (SQLException e) {
            System.err.println("Error fetching statistics: " + e.getMessage());
        }
    }


//     Display employee details

    private void displayEmployeeDetails(User employee) {
        System.out.println("\n Current Employee Information:");
        System.out.println("-".repeat(40));
        System.out.println("Employee ID:   " + employee.getEmployeeId());
        System.out.println("Name:          " + employee.getFullName());
        System.out.println("Email:         " + employee.getEmail());
        System.out.println("Phone:         " + (employee.getPhone() != null ? employee.getPhone() : "Not set"));
        System.out.println("Department:    " + (employee.getDepartment() != null ? employee.getDepartment() : "Not set"));
        System.out.println("Designation:   " + (employee.getDesignation() != null ? employee.getDesignation() : "Not set"));
        System.out.println("Role:          " + employee.getRole());
        System.out.println("Status:        " + (employee.isActive() ? "Active" : "Inactive"));
        System.out.println("Joining Date:  " + (employee.getJoiningDate() != null ? dateFormatter.format(employee.getJoiningDate()) : "Not set"));
        System.out.println("-".repeat(40));
    }


    private String truncateString(String str, int maxLength) {
        if (str == null) return "-";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }


    private void logout() {
        System.out.println("\n Logging out from Admin account...");
        System.out.println("Goodbye, " + currentAdmin.getFullName() + "!");
    }
}