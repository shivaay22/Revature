package com.revworkforce.controller;

import com.revworkforce.model.*;
import com.revworkforce.service.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class ManagerController {

    private User currentManager;
    private UserService userService;
    private LeaveService leaveService;
    private PerformanceService performanceService;
    private NotificationService notificationService;
    private HolidayService holidayService;
    private Scanner scanner;
    private SimpleDateFormat dateFormatter;

    public ManagerController(User manager) {
        this.currentManager = manager;
        this.userService = new UserService();
        this.leaveService = new LeaveService();
        this.performanceService = new PerformanceService();
        this.notificationService = new NotificationService();
        this.holidayService = new HolidayService();
        this.scanner = new Scanner(System.in);
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * Main menu for Manager dashboard
     */
    public void showManagerMenu() {
        while (true) {
            displayMenuHeader();

            int choice = getUserChoice();

            switch (choice) {
                // Employee self-service features
                case 1:
                    viewProfile();
                    break;
                case 2:
                    editProfile();
                    break;
                case 3:
                    viewMyLeaveBalance();
                    break;
                case 4:
                    applyForLeave();
                    break;
                case 5:
                    viewMyLeaveRequests();
                    break;
                case 6:
                    viewMyPerformanceReviews();
                    break;
                case 7:
                    manageMyGoals();
                    break;

                // Manager-specific features
                case 8:
                    viewTeamMembers();
                    break;
                case 9:
                    manageTeamLeaveRequests();
                    break;
                case 10:
                    reviewTeamPerformance();
                    break;
                case 11:
                    viewTeamGoals();
                    break;
                case 12:
                    generateTeamReport();
                    break;
                case 13:
                    viewTeamLeaveCalendar();
                    break;
                case 14:
                    viewTeamAttendanceSummary();
                    break;
                case 15:
                    viewNotifications();
                    break;
                case 16:
                    changePassword();
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
        System.out.println("         MANAGER DASHBOARD");
        System.out.println("=".repeat(60));
        System.out.println("Welcome, " + currentManager.getFullName() + " (Manager)");

        // Show team size and unread notifications
        try {
            List<User> teamMembers = userService.getEmployeesByManager(currentManager.getUserId());
            int unreadCount = notificationService.getUnreadCount(currentManager.getUserId());

            System.out.println(" Team Size: " + teamMembers.size() + " members");
            if (unreadCount > 0) {
                System.out.println(" Unread Notifications: " + unreadCount);
            }
        } catch (SQLException e) {
            // Silently ignore
        }

        System.out.println("\n--- MY WORKSPACE ---");
        System.out.println("1.  View My Profile");
        System.out.println("2.  Edit My Profile");
        System.out.println("3.  View My Leave Balance");
        System.out.println("4.  Apply for Leave");
        System.out.println("5.  View My Leave Requests");
        System.out.println("6.  View My Performance Reviews");
        System.out.println("7.  Manage My Goals");

        System.out.println("\n--- TEAM MANAGEMENT ---");
        System.out.println("8.  View Team Members");
        System.out.println("9.  Manage Team Leave Requests");
        System.out.println("10. Review Team Performance");
        System.out.println("11. View Team Goals");
        System.out.println("12. Generate Team Performance Report");
        System.out.println("13. View Team Leave Calendar");
        System.out.println("14. View Team Attendance Summary");

        System.out.println("\n--- COMMUNICATION ---");
        System.out.println("15. View Notifications");

        System.out.println("\n--- ACCOUNT ---");
        System.out.println("16. Change Password");
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

    // ==================== EMPLOYEE SELF-SERVICE METHODS ====================

    private void viewProfile() {
        System.out.println("\n=== My Profile ===");
        System.out.println("-".repeat(50));
        System.out.println("Employee ID:   " + currentManager.getEmployeeId());
        System.out.println("Full Name:     " + currentManager.getFullName());
        System.out.println("Email:         " + currentManager.getEmail());
        System.out.println("Phone:         " + (currentManager.getPhone() != null ? currentManager.getPhone() : "Not set"));
        System.out.println("Department:    " + (currentManager.getDepartment() != null ? currentManager.getDepartment() : "Not set"));
        System.out.println("Designation:   " + (currentManager.getDesignation() != null ? currentManager.getDesignation() : "Not set"));
        System.out.println("Role:          " + currentManager.getRole());
        System.out.println("Joining Date:  " + (currentManager.getJoiningDate() != null ?
                dateFormatter.format(currentManager.getJoiningDate()) : "Not set"));
        System.out.println("Address:       " + (currentManager.getAddress() != null ? currentManager.getAddress() : "Not set"));
        System.out.println("-".repeat(50));
    }

    private void editProfile() {
        System.out.println("\n=== Edit Profile ===");
        System.out.println("Leave fields blank to keep current values.");

        System.out.print("Phone (10 digits) [" + (currentManager.getPhone() != null ? currentManager.getPhone() : "Not set") + "]: ");
        String phone = scanner.nextLine();

        System.out.print("Address [" + (currentManager.getAddress() != null ? currentManager.getAddress() : "Not set") + "]: ");
        String address = scanner.nextLine();

        System.out.print("Emergency Contact [" + (currentManager.getEmergencyContact() != null ? currentManager.getEmergencyContact() : "Not set") + "]: ");
        String emergencyContact = scanner.nextLine();

        if (phone.isEmpty() && address.isEmpty() && emergencyContact.isEmpty()) {
            System.out.println("No changes made.");
            return;
        }

        try {
            if (userService.updateProfile(currentManager.getUserId(), phone, address, emergencyContact)) {
                // Refresh current manager object
                currentManager = userService.getUserById(currentManager.getUserId());
                System.out.println(" Profile updated successfully!");
            } else {
                System.out.println(" Failed to update profile.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
        }
    }

    private void viewMyLeaveBalance() {
        System.out.println("\n=== My Leave Balance ===");
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        System.out.println("-".repeat(40));
        System.out.printf("%-12s %-10s %-10s %-10s\n", "Leave Type", "Total", "Used", "Available");
        System.out.println("-".repeat(40));

        try {
            for (LeaveRequest.LeaveType leaveType : LeaveRequest.LeaveType.values()) {
                LeaveBalance balance = leaveService.getLeaveBalance(currentManager.getUserId(), leaveType, currentYear);
                if (balance != null) {
                    System.out.printf("%-12s %-10d %-10d %-10d\n",
                            leaveType, balance.getTotalDays(), balance.getUsedDays(), balance.getAvailableDays());
                } else {
                    System.out.printf("%-12s %-10s %-10s %-10s\n", leaveType, "N/A", "N/A", "N/A");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching leave balance: " + e.getMessage());
        }
        System.out.println("-".repeat(40));
    }

    private void applyForLeave() {
        System.out.println("\n=== Apply for Leave ===");

        // Display leave types
        System.out.println("\nLeave Types:");
        LeaveRequest.LeaveType[] leaveTypes = LeaveRequest.LeaveType.values();
        for (int i = 0; i < leaveTypes.length; i++) {
            System.out.println((i + 1) + ". " + leaveTypes[i]);
        }

        System.out.print("Select leave type: ");
        int typeChoice = scanner.nextInt();
        scanner.nextLine();

        if (typeChoice < 1 || typeChoice > leaveTypes.length) {
            System.out.println(" Invalid leave type.");
            return;
        }

        LeaveRequest.LeaveType selectedType = leaveTypes[typeChoice - 1];

        System.out.print("Start Date (YYYY-MM-DD): ");
        String startDateStr = scanner.nextLine();
        System.out.print("End Date (YYYY-MM-DD): ");
        String endDateStr = scanner.nextLine();
        System.out.print("Reason for leave: ");
        String reason = scanner.nextLine();

        try {
            Date startDate = dateFormatter.parse(startDateStr);
            Date endDate = dateFormatter.parse(endDateStr);
            Date today = new Date();

            if (startDate.before(today)) {
                System.out.println(" Start date cannot be in the past.");
                return;
            }

            if (startDate.after(endDate)) {
                System.out.println(" Start date must be before end date.");
                return;
            }

            if (leaveService.applyLeave(currentManager.getUserId(), selectedType, startDate, endDate, reason)) {
                System.out.println(" Leave application submitted successfully!");

                // Notify the manager's manager (if any)
                User reportingManager = userService.getReportingManager(currentManager.getUserId());
                if (reportingManager != null) {
                    notificationService.sendNotification(
                            reportingManager.getUserId(),
                            "Leave Request from Manager",
                            currentManager.getFullName() + " has applied for leave.",
                            Notification.NotificationType.LEAVE
                    );
                }
            } else {
                System.out.println(" Failed to submit leave application.");
            }
        } catch (ParseException e) {
            System.out.println(" Invalid date format. Please use YYYY-MM-DD.");
        } catch (IllegalArgumentException e) {
            System.out.println(" " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private void viewMyLeaveRequests() {
        System.out.println("\n=== My Leave Requests ===");
        System.out.println("-".repeat(80));
        System.out.printf("%-4s %-12s %-12s %-12s %-10s %s\n",
                "ID", "Type", "Start Date", "End Date", "Status", "Comments");
        System.out.println("-".repeat(80));

        try {
            List<LeaveRequest> requests = leaveService.getMyLeaveRequests(currentManager.getUserId());
            if (requests.isEmpty()) {
                System.out.println("No leave requests found.");
            } else {
                for (LeaveRequest request : requests) {
                    System.out.printf("%-4d %-12s %-12s %-12s %-10s %s\n",
                            request.getRequestId(),
                            request.getLeaveType(),
                            dateFormatter.format(request.getStartDate()),
                            dateFormatter.format(request.getEndDate()),
                            request.getStatus(),
                            request.getManagerComments() != null ? request.getManagerComments() : "-");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching leave requests: " + e.getMessage());
        }
        System.out.println("-".repeat(80));
    }

    private void viewMyPerformanceReviews() {
        System.out.println("\n=== My Performance Reviews ===");
        System.out.print("Enter review year (or press Enter for current year): ");
        String yearInput = scanner.nextLine();
        int year = yearInput.isEmpty() ? java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) : Integer.parseInt(yearInput);

        try {
            PerformanceReview review = performanceService.getPerformanceReview(currentManager.getUserId(), year);
            if (review == null) {
                System.out.println("No performance review found for " + year);
                System.out.print("Would you like to create one? (yes/no): ");
                if (scanner.nextLine().equalsIgnoreCase("yes")) {
                    createPerformanceReview(year);
                }
            } else {
                displayPerformanceReview(review);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching performance review: " + e.getMessage());
        }
    }

    private void createPerformanceReview(int year) throws SQLException {
        PerformanceReview review = new PerformanceReview(currentManager.getUserId(), year);

        System.out.println("\nCreating Performance Review for " + year);
        System.out.print("Key Deliverables Achieved: ");
        review.setKeyDeliverables(scanner.nextLine());

        System.out.print("Major Accomplishments: ");
        review.setMajorAccomplishments(scanner.nextLine());

        System.out.print("Areas of Improvement: ");
        review.setAreasImprovement(scanner.nextLine());

        System.out.print("Self Assessment Rating (1-5): ");
        double rating = scanner.nextDouble();
        scanner.nextLine();
        review.setSelfRating(rating);

        if (performanceService.createOrUpdatePerformanceReview(review)) {
            System.out.println(" Performance review saved successfully!");
            System.out.print("Do you want to submit it for review? (yes/no): ");
            if (scanner.nextLine().equalsIgnoreCase("yes")) {
                submitPerformanceReview(year);
            }
        } else {
            System.out.println(" Failed to save performance review.");
        }
    }

    private void submitPerformanceReview(int year) throws SQLException {
        if (performanceService.submitPerformanceReview(currentManager.getUserId(), year)) {
            System.out.println(" Performance review submitted for manager review!");

            // Notify manager's manager
            User reportingManager = userService.getReportingManager(currentManager.getUserId());
            if (reportingManager != null) {
                notificationService.sendNotification(
                        reportingManager.getUserId(),
                        "Performance Review Submitted",
                        currentManager.getFullName() + " has submitted their performance review.",
                        Notification.NotificationType.PERFORMANCE
                );
            }
        } else {
            System.out.println(" Failed to submit performance review.");
        }
    }

    private void displayPerformanceReview(PerformanceReview review) {
        System.out.println("\n Performance Review Details:");
        System.out.println("-".repeat(50));
        System.out.println("Year: " + review.getReviewYear());
        System.out.println("Status: " + review.getStatus());
        System.out.println("\nKey Deliverables:");
        System.out.println("  " + (review.getKeyDeliverables() != null ? review.getKeyDeliverables() : "Not provided"));
        System.out.println("\nMajor Accomplishments:");
        System.out.println("  " + (review.getMajorAccomplishments() != null ? review.getMajorAccomplishments() : "Not provided"));
        System.out.println("\nAreas of Improvement:");
        System.out.println("  " + (review.getAreasImprovement() != null ? review.getAreasImprovement() : "Not provided"));
        System.out.println("\nSelf Rating: " + (review.getSelfRating() != null ? review.getSelfRating() : "Not rated"));

        if (review.getManagerRating() != null) {
            System.out.println("\nManager's Rating: " + review.getManagerRating());
            System.out.println("Manager's Feedback: " + review.getManagerFeedback());
        }
        System.out.println("-".repeat(50));

        if (review.getStatus() == PerformanceReview.ReviewStatus.DRAFT) {
            System.out.print("Do you want to submit this review? (yes/no): ");
            if (scanner.nextLine().equalsIgnoreCase("yes")) {
                try {
                    submitPerformanceReview(review.getReviewYear());
                } catch (SQLException e) {
                    System.err.println("Error submitting review: " + e.getMessage());
                }
            }
        }
    }

    private void manageMyGoals() {
        while (true) {
            System.out.println("\n=== My Goals ===");
            System.out.println("1. View All My Goals");
            System.out.println("2. Add New Goal");
            System.out.println("3. Update Goal Progress");
            System.out.println("4. Edit Goal");
            System.out.println("5. Back");
            System.out.print("Select option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewMyGoals();
                    break;
                case 2:
                    addNewGoal();
                    break;
                case 3:
                    updateGoalProgress();
                    break;
                case 4:
                    editGoal();
                    break;
                case 5:
                    return;
                default:
                    System.out.println(" Invalid option.");
            }
        }
    }

    private void viewMyGoals() {
        System.out.println("\n=== My Goals ===");
        System.out.println("-".repeat(80));

        try {
            List<Goal> goals = performanceService.getMyGoals(currentManager.getUserId());
            if (goals.isEmpty()) {
                System.out.println("No goals found. Add your first goal!");
            } else {
                for (Goal goal : goals) {
                    System.out.println("\n Goal ID: " + goal.getGoalId());
                    System.out.println("   Description: " + goal.getGoalDescription());
                    System.out.println("   Priority: " + goal.getPriority());
                    System.out.println("   Deadline: " + (goal.getDeadline() != null ?
                            dateFormatter.format(goal.getDeadline()) : "No deadline"));
                    System.out.println("   Progress: " + goal.getProgressPercentage() + "%");
                    System.out.println("   Status: " + goal.getStatus());
                    System.out.println("   Success Metrics: " + (goal.getSuccessMetrics() != null ?
                            goal.getSuccessMetrics() : "Not specified"));
                    if (goal.getManagerFeedback() != null) {
                        System.out.println("   Manager Feedback: " + goal.getManagerFeedback());
                    }
                    System.out.println("-".repeat(40));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching goals: " + e.getMessage());
        }
    }

    private void addNewGoal() {
        System.out.println("\n=== Add New Goal ===");
        System.out.print("Goal Description: ");
        String description = scanner.nextLine();

        System.out.print("Deadline (YYYY-MM-DD, optional): ");
        String deadlineStr = scanner.nextLine();
        Date deadline = null;
        try {
            if (!deadlineStr.isEmpty()) {
                deadline = dateFormatter.parse(deadlineStr);
            }
        } catch (ParseException e) {
            System.out.println(" Invalid date format. Skipping deadline.");
        }

        System.out.print("Priority (HIGH/MEDIUM/LOW): ");
        String priorityStr = scanner.nextLine().toUpperCase();
        Goal.Priority priority = Goal.Priority.MEDIUM;
        try {
            priority = Goal.Priority.valueOf(priorityStr);
        } catch (IllegalArgumentException e) {
            System.out.println(" Invalid priority. Using MEDIUM.");
        }

        System.out.print("Success Metrics (how to measure success): ");
        String metrics = scanner.nextLine();

        Goal goal = new Goal(currentManager.getUserId(), description, priority);
        goal.setDeadline(deadline);
        goal.setSuccessMetrics(metrics);

        try {
            if (performanceService.createGoal(goal)) {
                System.out.println(" Goal added successfully!");
            } else {
                System.out.println(" Failed to add goal.");
            }
        } catch (SQLException e) {
            System.err.println("Error adding goal: " + e.getMessage());
        }
    }

    private void updateGoalProgress() {
        try {
            List<Goal> goals = performanceService.getMyGoals(currentManager.getUserId());
            if (goals.isEmpty()) {
                System.out.println("No goals found to update.");
                return;
            }

            System.out.println("\n=== Update Goal Progress ===");
            System.out.println("Your Goals:");
            for (Goal goal : goals) {
                System.out.printf("  %d. %s (Progress: %d%%)\n",
                        goal.getGoalId(),
                        goal.getGoalDescription().length() > 50 ?
                                goal.getGoalDescription().substring(0, 47) + "..." : goal.getGoalDescription(),
                        goal.getProgressPercentage());
            }

            System.out.print("\nEnter Goal ID to update: ");
            int goalId = scanner.nextInt();
            System.out.print("Enter new progress percentage (0-100): ");
            int progress = scanner.nextInt();
            scanner.nextLine();

            if (progress < 0 || progress > 100) {
                System.out.println(" Progress must be between 0 and 100.");
                return;
            }

            if (performanceService.updateGoalProgress(goalId, progress)) {
                System.out.println(" Goal progress updated successfully!");

                // If goal is completed, notify manager
                if (progress == 100) {
                    User reportingManager = userService.getReportingManager(currentManager.getUserId());
                    if (reportingManager != null) {
                        notificationService.sendNotification(
                                reportingManager.getUserId(),
                                "Goal Completed",
                                currentManager.getFullName() + " has completed a goal.",
                                Notification.NotificationType.PERFORMANCE
                        );
                    }
                }
            } else {
                System.out.println(" Failed to update goal progress.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating goal: " + e.getMessage());
        }
    }

    private void editGoal() {
        try {
            List<Goal> goals = performanceService.getMyGoals(currentManager.getUserId());
            if (goals.isEmpty()) {
                System.out.println("No goals found to edit.");
                return;
            }

            System.out.println("\n=== Edit Goal ===");
            for (Goal goal : goals) {
                System.out.printf("  %d. %s\n", goal.getGoalId(), goal.getGoalDescription());
            }

            System.out.print("Enter Goal ID to edit: ");
            int goalId = scanner.nextInt();
            scanner.nextLine();

            Goal goal = goals.stream().filter(g -> g.getGoalId() == goalId).findFirst().orElse(null);
            if (goal == null) {
                System.out.println(" Goal not found.");
                return;
            }

            System.out.print("Goal Description [" + goal.getGoalDescription() + "]: ");
            String description = scanner.nextLine();
            if (!description.isEmpty()) goal.setGoalDescription(description);

            System.out.print("Priority [" + goal.getPriority() + "]: ");
            String priorityStr = scanner.nextLine().toUpperCase();
            if (!priorityStr.isEmpty()) {
                try {
                    goal.setPriority(Goal.Priority.valueOf(priorityStr));
                } catch (IllegalArgumentException e) {
                    System.out.println(" Invalid priority. Keeping current.");
                }
            }

            System.out.print("Success Metrics [" + (goal.getSuccessMetrics() != null ? goal.getSuccessMetrics() : "Not set") + "]: ");
            String metrics = scanner.nextLine();
            if (!metrics.isEmpty()) goal.setSuccessMetrics(metrics);

            if (performanceService.updateGoal(goal)) {
                System.out.println(" Goal updated successfully!");
            } else {
                System.out.println(" Failed to update goal.");
            }
        } catch (SQLException e) {
            System.err.println("Error editing goal: " + e.getMessage());
        }
    }

    // ==================== MANAGER-SPECIFIC METHODS ====================

    private void viewTeamMembers() {
        System.out.println("\n=== Team Members ===");
        System.out.println("-".repeat(100));
        System.out.printf("%-12s %-25s %-20s %-15s %-12s\n",
                "Emp ID", "Name", "Email", "Department", "Status");
        System.out.println("-".repeat(100));

        try {
            List<User> teamMembers = userService.getEmployeesByManager(currentManager.getUserId());
            if (teamMembers.isEmpty()) {
                System.out.println("No team members assigned to you.");
            } else {
                for (User member : teamMembers) {
                    System.out.printf("%-12s %-25s %-20s %-15s %-12s\n",
                            member.getEmployeeId(),
                            truncateString(member.getFullName(), 25),
                            truncateString(member.getEmail(), 20),
                            member.getDepartment() != null ? member.getDepartment() : "-",
                            member.isActive() ? "Active" : "Inactive");
                }
                System.out.println("-".repeat(100));
                System.out.println("Total Team Members: " + teamMembers.size());
            }
        } catch (SQLException e) {
            System.err.println("Error fetching team members: " + e.getMessage());
        }
    }

    private void manageTeamLeaveRequests() {
        while (true) {
            System.out.println("\n=== Team Leave Management ===");
            System.out.println("1. View Pending Leave Requests");
            System.out.println("2. View All Team Leave Requests");
            System.out.println("3. Approve/Reject Leave Request");
            System.out.println("4. View Team Member Leave Balance");
            System.out.println("5. Back");
            System.out.print("Select option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewPendingLeaveRequests();
                    break;
                case 2:
                    viewAllTeamLeaveRequests();
                    break;
                case 3:
                    processLeaveRequest();
                    break;
                case 4:
                    viewTeamMemberLeaveBalance();
                    break;
                case 5:
                    return;
                default:
                    System.out.println(" Invalid option.");
            }
        }
    }

    private void viewPendingLeaveRequests() {
        System.out.println("\n=== Pending Leave Requests ===");
        System.out.println("-".repeat(80));

        try {
            List<LeaveRequest> requests = leaveService.getTeamLeaveRequests(currentManager.getUserId());
            if (requests.isEmpty()) {
                System.out.println("No pending leave requests.");
            } else {
                for (LeaveRequest request : requests) {
                    User employee = userService.getUserById(request.getUserId());
                    System.out.println("\n Request ID: " + request.getRequestId());
                    System.out.println("   Employee: " + (employee != null ? employee.getFullName() : "Unknown"));
                    System.out.println("   Leave Type: " + request.getLeaveType());
                    System.out.println("   Duration: " + dateFormatter.format(request.getStartDate()) +
                            " to " + dateFormatter.format(request.getEndDate()));
                    System.out.println("   Days: " + request.getNumberOfDays());
                    System.out.println("   Reason: " + request.getReason());
                    System.out.println("-".repeat(40));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching leave requests: " + e.getMessage());
        }
    }

    private void viewAllTeamLeaveRequests() {
        System.out.println("\n=== All Team Leave Requests ===");
        System.out.println("-".repeat(100));
        System.out.printf("%-4s %-20s %-12s %-12s %-12s %-10s\n",
                "ID", "Employee", "Type", "Start Date", "End Date", "Status");
        System.out.println("-".repeat(100));

        try {
            List<LeaveRequest> requests = leaveService.getAllTeamLeaveRequests(currentManager.getUserId());
            if (requests.isEmpty()) {
                System.out.println("No leave requests found.");
            } else {
                for (LeaveRequest request : requests) {
                    User employee = userService.getUserById(request.getUserId());
                    System.out.printf("%-4d %-20s %-12s %-12s %-12s %-10s\n",
                            request.getRequestId(),
                            employee != null ? truncateString(employee.getFullName(), 20) : "Unknown",
                            request.getLeaveType(),
                            dateFormatter.format(request.getStartDate()),
                            dateFormatter.format(request.getEndDate()),
                            request.getStatus());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching leave requests: " + e.getMessage());
        }
        System.out.println("-".repeat(100));
    }

    private void processLeaveRequest() {
        System.out.println("\n=== Process Leave Request ===");
        System.out.print("Enter Request ID: ");
        int requestId = scanner.nextInt();
        scanner.nextLine();

        try {
            // Get request details (simplified - would need direct DAO method)
            System.out.print("Comments for employee: ");
            String comments = scanner.nextLine();

            System.out.print("Action: (A)pprove or (R)eject: ");
            String action = scanner.nextLine().toUpperCase();

            boolean success;
            if (action.equals("A")) {
                success = leaveService.approveLeave(requestId, currentManager.getUserId(), comments);
                if (success) {
                    System.out.println(" Leave request approved successfully!");
                }
            } else if (action.equals("R")) {
                success = leaveService.rejectLeave(requestId, currentManager.getUserId(), comments);
                if (success) {
                    System.out.println(" Leave request rejected successfully!");
                }
            } else {
                System.out.println(" Invalid action.");
                return;
            }

            if (!success) {
                System.out.println(" Failed to process leave request.");
            }
        } catch (SQLException e) {
            System.err.println("Error processing leave request: " + e.getMessage());
        }
    }

    private void viewTeamMemberLeaveBalance() {
        System.out.println("\n=== Team Member Leave Balance ===");

        try {
            List<User> teamMembers = userService.getEmployeesByManager(currentManager.getUserId());
            if (teamMembers.isEmpty()) {
                System.out.println("No team members found.");
                return;
            }

            System.out.println("\nSelect team member:");
            for (int i = 0; i < teamMembers.size(); i++) {
                System.out.println((i + 1) + ". " + teamMembers.get(i).getFullName());
            }
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice < 1 || choice > teamMembers.size()) {
                System.out.println(" Invalid choice.");
                return;
            }

            User member = teamMembers.get(choice - 1);
            int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

            System.out.println("\nLeave Balance for " + member.getFullName() + " (" + currentYear + ")");
            System.out.println("-".repeat(50));
            System.out.printf("%-12s %-10s %-10s %-10s\n", "Leave Type", "Total", "Used", "Available");
            System.out.println("-".repeat(50));

            for (LeaveRequest.LeaveType leaveType : LeaveRequest.LeaveType.values()) {
                LeaveBalance balance = leaveService.getLeaveBalance(member.getUserId(), leaveType, currentYear);
                if (balance != null) {
                    System.out.printf("%-12s %-10d %-10d %-10d\n",
                            leaveType, balance.getTotalDays(), balance.getUsedDays(), balance.getAvailableDays());
                }
            }
            System.out.println("-".repeat(50));

        } catch (SQLException e) {
            System.err.println("Error fetching leave balance: " + e.getMessage());
        }
    }

    private void reviewTeamPerformance() {
        System.out.println("\n=== Review Team Performance ===");

        try {
            List<PerformanceReview> reviews = performanceService.getTeamPerformanceReviews(currentManager.getUserId());
            if (reviews.isEmpty()) {
                System.out.println("No performance reviews submitted for review.");
                return;
            }

            System.out.println("\nPending Performance Reviews:");
            for (int i = 0; i < reviews.size(); i++) {
                PerformanceReview review = reviews.get(i);
                User employee = userService.getUserById(review.getUserId());
                System.out.println((i + 1) + ". " + (employee != null ? employee.getFullName() : "Unknown") +
                        " - Year: " + review.getReviewYear());
            }

            System.out.print("Select review to provide feedback (0 to cancel): ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 0) return;
            if (choice < 1 || choice > reviews.size()) {
                System.out.println(" Invalid choice.");
                return;
            }

            PerformanceReview selected = reviews.get(choice - 1);
            User employee = userService.getUserById(selected.getUserId());

            System.out.println("\n Performance Review for " + employee.getFullName());
            System.out.println("-".repeat(50));
            System.out.println("Key Deliverables:");
            System.out.println("  " + (selected.getKeyDeliverables() != null ? selected.getKeyDeliverables() : "Not provided"));
            System.out.println("\nMajor Accomplishments:");
            System.out.println("  " + (selected.getMajorAccomplishments() != null ? selected.getMajorAccomplishments() : "Not provided"));
            System.out.println("\nAreas of Improvement:");
            System.out.println("  " + (selected.getAreasImprovement() != null ? selected.getAreasImprovement() : "Not provided"));
            System.out.println("\nSelf Rating: " + (selected.getSelfRating() != null ? selected.getSelfRating() : "Not rated"));
            System.out.println("-".repeat(50));

            System.out.print("\nEnter Manager Rating (1-5): ");
            double rating = scanner.nextDouble();
            scanner.nextLine();

            if (rating < 1 || rating > 5) {
                System.out.println(" Rating must be between 1 and 5.");
                return;
            }

            System.out.print("Enter Detailed Feedback: ");
            String feedback = scanner.nextLine();

            if (performanceService.providePerformanceFeedback(selected.getReviewId(), rating, feedback)) {
                System.out.println(" Feedback submitted successfully!");

                // Notify employee
                notificationService.sendNotification(
                        selected.getUserId(),
                        "Performance Review Feedback Received",
                        "Your manager has provided feedback on your performance review.",
                        Notification.NotificationType.PERFORMANCE
                );
            } else {
                System.out.println(" Failed to submit feedback.");
            }
        } catch (SQLException e) {
            System.err.println("Error processing performance review: " + e.getMessage());
        }
    }

    private void viewTeamGoals() {
        System.out.println("\n=== Team Goals Overview ===");
        System.out.println("-".repeat(100));
        System.out.printf("%-20s %-40s %-10s %-15s\n", "Employee", "Goal", "Progress", "Status");
        System.out.println("-".repeat(100));

        try {
            List<Goal> goals = performanceService.getTeamGoals(currentManager.getUserId());
            if (goals.isEmpty()) {
                System.out.println("No goals found for team members.");
            } else {
                Map<Integer, List<Goal>> goalsByEmployee = goals.stream()
                        .collect(Collectors.groupingBy(Goal::getUserId));

                for (Map.Entry<Integer, List<Goal>> entry : goalsByEmployee.entrySet()) {
                    User employee = userService.getUserById(entry.getKey());
                    String employeeName = employee != null ? employee.getFullName() : "Unknown";

                    for (Goal goal : entry.getValue()) {
                        System.out.printf("%-20s %-40s %-10d%% %-15s\n",
                                truncateString(employeeName, 20),
                                truncateString(goal.getGoalDescription(), 40),
                                goal.getProgressPercentage(),
                                goal.getStatus());
                    }
                }
            }
            System.out.println("-".repeat(100));

            System.out.print("\nEnter Goal ID to provide feedback (0 to skip): ");
            int goalId = scanner.nextInt();
            scanner.nextLine();

            if (goalId != 0) {
                provideGoalFeedback(goalId);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching team goals: " + e.getMessage());
        }
    }

    private void provideGoalFeedback(int goalId) {
        try {
            System.out.print("Enter your feedback: ");
            String feedback = scanner.nextLine();

            if (performanceService.provideGoalFeedback(goalId, feedback)) {
                System.out.println(" Feedback provided successfully!");

                // Get goal to notify employee (simplified)
                System.out.println("Notification sent to employee.");
            } else {
                System.out.println(" Failed to provide feedback.");
            }
        } catch (SQLException e) {
            System.err.println("Error providing feedback: " + e.getMessage());
        }
    }

    private void generateTeamReport() {
        System.out.println("\n=== Team Performance Report ===");
        System.out.println("=".repeat(60));
        System.out.println("Generated on: " + new Date());
        System.out.println("Manager: " + currentManager.getFullName());
        System.out.println("=".repeat(60));

        try {
            List<User> teamMembers = userService.getEmployeesByManager(currentManager.getUserId());
            if (teamMembers.isEmpty()) {
                System.out.println("No team members found.");
                return;
            }

            System.out.println("\n TEAM SUMMARY");
            System.out.println("-".repeat(40));
            System.out.println("Total Team Size: " + teamMembers.size());

            long activeMembers = teamMembers.stream().filter(User::isActive).count();
            System.out.println("Active Members: " + activeMembers);
            System.out.println("Inactive Members: " + (teamMembers.size() - activeMembers));

            // Performance summary
            int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            System.out.println("\n PERFORMANCE SUMMARY (" + currentYear + ")");
            System.out.println("-".repeat(80));
            System.out.printf("%-25s %-15s %-20s\n", "Employee", "Avg Rating", "Goals Completed");
            System.out.println("-".repeat(80));

            for (User member : teamMembers) {
                // Get performance rating
                PerformanceReview review = performanceService.getPerformanceReview(member.getUserId(), currentYear);
                String rating = (review != null && review.getManagerRating() != null) ?
                        String.valueOf(review.getManagerRating()) : "Not reviewed";

                // Get goals completed
                List<Goal> goals = performanceService.getMyGoals(member.getUserId());
                long completedGoals = goals.stream()
                        .filter(g -> g.getStatus() == Goal.GoalStatus.COMPLETED)
                        .count();

                System.out.printf("%-25s %-15s %-20d\n",
                        truncateString(member.getFullName(), 25),
                        rating,
                        completedGoals);
            }

            // Leave summary
            System.out.println("\n LEAVE SUMMARY (" + currentYear + ")");
            System.out.println("-".repeat(80));
            System.out.printf("%-25s %-15s %-15s\n", "Employee", "Leaves Taken", "Available");
            System.out.println("-".repeat(80));

            for (User member : teamMembers) {
                int totalLeaves = 0;
                int totalAvailable = 0;

                for (LeaveRequest.LeaveType leaveType : LeaveRequest.LeaveType.values()) {
                    LeaveBalance balance = leaveService.getLeaveBalance(member.getUserId(), leaveType, currentYear);
                    if (balance != null) {
                        totalLeaves += balance.getUsedDays();
                        totalAvailable += balance.getAvailableDays();
                    }
                }

                System.out.printf("%-25s %-15d %-15d\n",
                        truncateString(member.getFullName(), 25),
                        totalLeaves,
                        totalAvailable);
            }

            System.out.println("\n" + "=".repeat(60));
            System.out.println("Report generated successfully!");

        } catch (SQLException e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }

    private void viewTeamLeaveCalendar() {
        System.out.println("\n=== Team Leave Calendar ===");
        System.out.print("Enter year (YYYY): ");
        int year = scanner.nextInt();
        System.out.print("Enter month (1-12): ");
        int month = scanner.nextInt();
        scanner.nextLine();

        System.out.printf("\nTeam Leave Calendar - %d/%d\n", month, year);
        System.out.println("-".repeat(60));

        try {
            List<LeaveRequest> requests = leaveService.getAllTeamLeaveRequests(currentManager.getUserId());
            boolean found = false;

            for (LeaveRequest request : requests) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(request.getStartDate());
                int requestYear = cal.get(Calendar.YEAR);
                int requestMonth = cal.get(Calendar.MONTH) + 1;

                if (requestYear == year && requestMonth == month &&
                        request.getStatus() == LeaveRequest.LeaveStatus.APPROVED) {
                    User employee = userService.getUserById(request.getUserId());
                    System.out.printf("%s (%s): %s - %s\n",
                            employee != null ? employee.getFullName() : "Unknown",
                            request.getLeaveType(),
                            dateFormatter.format(request.getStartDate()),
                            dateFormatter.format(request.getEndDate()));
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No approved leaves for this period.");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching leave calendar: " + e.getMessage());
        }
        System.out.println("-".repeat(60));
    }

    private void viewTeamAttendanceSummary() {
        System.out.println("\n=== Team Attendance Summary ===");
        System.out.print("Enter month (1-12): ");
        int month = scanner.nextInt();
        System.out.print("Enter year (YYYY): ");
        int year = scanner.nextInt();
        scanner.nextLine();

        System.out.printf("\nAttendance Summary - %d/%d\n", month, year);
        System.out.println("-".repeat(80));
        System.out.printf("%-25s %-15s %-20s\n", "Employee", "Leaves Taken", "Working Days");
        System.out.println("-".repeat(80));

        try {
            List<User> teamMembers = userService.getEmployeesByManager(currentManager.getUserId());

            for (User member : teamMembers) {
                // Calculate leaves taken in the month (simplified - would need more complex logic)
                int leavesInMonth = 0;
                List<LeaveRequest> requests = leaveService.getAllTeamLeaveRequests(currentManager.getUserId());

                for (LeaveRequest request : requests) {
                    if (request.getUserId() == member.getUserId() &&
                            request.getStatus() == LeaveRequest.LeaveStatus.APPROVED) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(request.getStartDate());
                        int reqYear = cal.get(Calendar.YEAR);
                        int reqMonth = cal.get(Calendar.MONTH) + 1;

                        if (reqYear == year && reqMonth == month) {
                            leavesInMonth += request.getNumberOfDays();
                        }
                    }
                }

                // Calculate working days in month (simplified)
                int workingDays = 22; // Approximate
                System.out.printf("%-25s %-15d %-20d\n",
                        truncateString(member.getFullName(), 25),
                        leavesInMonth,
                        workingDays - leavesInMonth);
            }
        } catch (SQLException e) {
            System.err.println("Error generating attendance summary: " + e.getMessage());
        }
        System.out.println("-".repeat(80));
    }

    private void viewNotifications() {
        System.out.println("\n=== Notifications ===");
        System.out.println("-".repeat(60));

        try {
            List<Notification> notifications = notificationService.getAllNotifications(currentManager.getUserId());
            if (notifications.isEmpty()) {
                System.out.println("No notifications.");
            } else {
                for (Notification notification : notifications) {
                    System.out.printf("\n[%s] %s\n", notification.getType(), notification.getTitle());
                    System.out.println(notification.getMessage());
                    System.out.println("Received: " + notification.getCreatedAt());
                    if (!notification.isRead()) {
                        System.out.println(" NEW");
                        notificationService.markAsRead(notification.getNotificationId());
                    }
                    System.out.println("-".repeat(40));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching notifications: " + e.getMessage());
        }
    }

    private void changePassword() {
        System.out.println("\n=== Change Password ===");
        System.out.print("Current Password: ");
        String oldPassword = scanner.nextLine();
        System.out.print("New Password: ");
        String newPassword = scanner.nextLine();
        System.out.print("Confirm New Password: ");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println(" Passwords do not match!");
            return;
        }

        if (newPassword.length() < 6) {
            System.out.println(" Password must be at least 6 characters long!");
            return;
        }

        try {
            if (userService.changePassword(currentManager.getUserId(), oldPassword, newPassword)) {
                System.out.println(" Password changed successfully!");
            } else {
                System.out.println(" Current password is incorrect!");
            }
        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
        }
    }

    private void logout() {
        System.out.println("\n Logging out from Manager account...");
        System.out.println("Goodbye, " + currentManager.getFullName() + "!");
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) return "-";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}