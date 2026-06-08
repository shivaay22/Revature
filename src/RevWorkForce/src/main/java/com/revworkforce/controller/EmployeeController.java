package com.revworkforce.controller;

import com.revworkforce.model.*;
import com.revworkforce.service.*;
import com.revworkforce.utils.InputValidator;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class EmployeeController {
    private User currentUser;
    private UserService userService;
    private LeaveService leaveService;
    private PerformanceService performanceService;
    private NotificationService notificationService;
    private HolidayService holidayService;
    private AnnouncementService announcementService;
    private Scanner scanner;
    private SimpleDateFormat dateFormat;

    public EmployeeController(User user) {
        this.currentUser = user;
        this.userService = new UserService();
        this.leaveService = new LeaveService();
        this.performanceService = new PerformanceService();
        this.notificationService = new NotificationService();
        this.holidayService = new HolidayService();
        this.announcementService = new AnnouncementService();
        this.scanner = new Scanner(System.in);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== Employee Dashboard ===");
            System.out.println("Welcome, " + currentUser.getFullName());

            try {
                int unreadCount = notificationService.getUnreadCount(currentUser.getUserId());
                if (unreadCount > 0) {
                    System.out.println(" You have " + unreadCount + " unread notification(s)");
                }
            } catch (SQLException e) {
                // Ignore
            }

            System.out.println("\n1. View/Edit Profile");
            System.out.println("2. View Reporting Manager");
            System.out.println("3. Leave Management");
            System.out.println("4. Performance Management");
            System.out.println("5. View Holidays");
            System.out.println("6. View Announcements");
            System.out.println("7. View Notifications");
            System.out.println("8. Employee Directory");
            System.out.println("9. Change Password");
            System.out.println("0. Logout");
            System.out.print("\nSelect option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    editProfile();
                    break;
                case 2:
                    viewReportingManager();
                    break;
                case 3:
                    leaveManagement();
                    break;
                case 4:
                    performanceManagement();
                    break;
                case 5:
                    viewHolidays();
                    break;
                case 6:
                    viewAnnouncements();
                    break;
                case 7:
                    viewNotifications();
                    break;
                case 8:
                    viewEmployeeDirectory();
                    break;
                case 9:
                    changePassword();
                    break;
                case 0:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void editProfile() {
        System.out.println("\n=== Edit Profile ===");
        System.out.println("Current Phone: " + (currentUser.getPhone() != null ? currentUser.getPhone() : "Not set"));
        System.out.print("New Phone (10 digits): ");
        String phone = scanner.nextLine();

        System.out.println("Current Address: " + (currentUser.getAddress() != null ? currentUser.getAddress() : "Not set"));
        System.out.print("New Address: ");
        String address = scanner.nextLine();

        System.out.println("Current Emergency Contact: " + (currentUser.getEmergencyContact() != null ? currentUser.getEmergencyContact() : "Not set"));
        System.out.print("New Emergency Contact (10 digits): ");
        String emergencyContact = scanner.nextLine();

        if (phone.isEmpty() && address.isEmpty() && emergencyContact.isEmpty()) {
            System.out.println("No changes made.");
            return;
        }

        try {
            if (userService.updateProfile(currentUser.getUserId(), phone, address, emergencyContact)) {
                // Update current user object
                User updatedUser = userService.getUserById(currentUser.getUserId());
                if (updatedUser != null) {
                    currentUser = updatedUser;
                }
                System.out.println(" Profile updated successfully!");
            } else {
                System.out.println(" Failed to update profile.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
        }
    }

    private void viewReportingManager() {
        System.out.println("\n=== Reporting Manager ===");
        try {
            User manager = userService.getReportingManager(currentUser.getUserId());
            if (manager != null) {
                System.out.println("Manager Name: " + manager.getFullName());
                System.out.println("Employee ID: " + manager.getEmployeeId());
                System.out.println("Email: " + manager.getEmail());
                System.out.println("Phone: " + (manager.getPhone() != null ? manager.getPhone() : "Not available"));
                System.out.println("Department: " + (manager.getDepartment() != null ? manager.getDepartment() : "Not specified"));
                System.out.println("Designation: " + (manager.getDesignation() != null ? manager.getDesignation() : "Not specified"));
            } else {
                System.out.println("No reporting manager assigned.");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching manager details: " + e.getMessage());
        }
    }

    private void leaveManagement() {
        while (true) {
            System.out.println("\n=== Leave Management ===");
            System.out.println("1. View Leave Balance");
            System.out.println("2. Apply for Leave");
            System.out.println("3. View My Leave Requests");
            System.out.println("4. Cancel Pending Leave");
            System.out.println("5. Back to Main Menu");
            System.out.print("\nSelect option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewLeaveBalance();
                    break;
                case 2:
                    applyForLeave();
                    break;
                case 3:
                    viewMyLeaveRequests();
                    break;
                case 4:
                    cancelPendingLeave();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void viewLeaveBalance() {
        System.out.println("\n=== Leave Balance ===");
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        try {
            for (LeaveRequest.LeaveType type : LeaveRequest.LeaveType.values()) {
                LeaveBalance balance = leaveService.getLeaveBalance(currentUser.getUserId(), type, currentYear);
                if (balance != null) {
                    System.out.printf("%-12s: %d/%d days available\n",
                            type, balance.getAvailableDays(), balance.getTotalDays());
                } else {
                    System.out.printf("%-12s: Not configured\n", type);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching leave balance: " + e.getMessage());
        }
    }

    private void applyForLeave() {
        System.out.println("\n=== Apply for Leave ===");

        // Show leave types
        System.out.println("Leave Types:");
        LeaveRequest.LeaveType[] types = LeaveRequest.LeaveType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i]);
        }
        System.out.print("Select leave type: ");
        int typeChoice = scanner.nextInt();
        scanner.nextLine();

        if (typeChoice < 1 || typeChoice > types.length) {
            System.out.println("Invalid leave type.");
            return;
        }

        LeaveRequest.LeaveType selectedType = types[typeChoice - 1];

        System.out.print("Start Date (YYYY-MM-DD): ");
        String startDateStr = scanner.nextLine();
        System.out.print("End Date (YYYY-MM-DD): ");
        String endDateStr = scanner.nextLine();
        System.out.print("Reason: ");
        String reason = scanner.nextLine();

        try {
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);
            Date today = new Date();

            if (startDate.before(today)) {
                System.out.println(" Start date cannot be in the past.");
                return;
            }

            if (leaveService.applyLeave(currentUser.getUserId(), selectedType, startDate, endDate, reason)) {
                System.out.println(" Leave application submitted successfully!");

                // Notify manager
                User manager = userService.getReportingManager(currentUser.getUserId());
                if (manager != null) {
                    notificationService.sendNotification(
                            manager.getUserId(),
                            "New Leave Request",
                            currentUser.getFullName() + " has applied for " + selectedType + " leave from " + startDateStr + " to " + endDateStr,
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
        try {
            List<LeaveRequest> requests = leaveService.getMyLeaveRequests(currentUser.getUserId());
            if (requests.isEmpty()) {
                System.out.println("No leave requests found.");
            } else {
                System.out.printf("%-4s %-10s %-12s %-12s %-10s %s\n",
                        "ID", "Type", "Start Date", "End Date", "Status", "Comments");
                for (LeaveRequest request : requests) {
                    System.out.printf("%-4d %-10s %-12s %-12s %-10s %s\n",
                            request.getRequestId(),
                            request.getLeaveType(),
                            dateFormat.format(request.getStartDate()),
                            dateFormat.format(request.getEndDate()),
                            request.getStatus(),
                            request.getManagerComments() != null ? request.getManagerComments() : "-");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching leave requests: " + e.getMessage());
        }
    }

    private void cancelPendingLeave() {
        System.out.println("\n=== Cancel Pending Leave ===");
        try {
            List<LeaveRequest> pendingRequests = leaveService.getMyLeaveRequests(currentUser.getUserId());
            pendingRequests.removeIf(r -> r.getStatus() != LeaveRequest.LeaveStatus.PENDING);

            if (pendingRequests.isEmpty()) {
                System.out.println("No pending leave requests to cancel.");
                return;
            }

            System.out.println("Pending Leave Requests:");
            for (LeaveRequest request : pendingRequests) {
                System.out.printf("%d. %s from %s to %s\n",
                        request.getRequestId(),
                        request.getLeaveType(),
                        dateFormat.format(request.getStartDate()),
                        dateFormat.format(request.getEndDate()));
            }

            System.out.print("Enter Request ID to cancel (0 to cancel): ");
            int requestId = scanner.nextInt();
            scanner.nextLine();

            if (requestId == 0) return;

            if (leaveService.cancelLeaveRequest(requestId)) {
                System.out.println(" Leave request cancelled successfully!");
            } else {
                System.out.println(" Failed to cancel leave request.");
            }
        } catch (SQLException e) {
            System.err.println("Error cancelling leave: " + e.getMessage());
        }
    }

    private void performanceManagement() {
        while (true) {
            System.out.println("\n=== Performance Management ===");
            System.out.println("1. Create/Edit Performance Review");
            System.out.println("2. Submit Performance Review");
            System.out.println("3. View My Goals");
            System.out.println("4. Add/Update Goal");
            System.out.println("5. Update Goal Progress");
            System.out.println("6. View Manager Feedback");
            System.out.println("7. Back to Main Menu");
            System.out.print("\nSelect option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createEditPerformanceReview();
                    break;
                case 2:
                    submitPerformanceReview();
                    break;
                case 3:
                    viewMyGoals();
                    break;
                case 4:
                    addUpdateGoal();
                    break;
                case 5:
                    updateGoalProgress();
                    break;
                case 6:
                    viewManagerFeedback();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void createEditPerformanceReview() {
        System.out.println("\n=== Performance Review ===");
        System.out.print("Enter review year: ");
        int year = scanner.nextInt();
        scanner.nextLine();

        try {
            PerformanceReview review = performanceService.getPerformanceReview(currentUser.getUserId(), year);
            if (review == null) {
                review = new PerformanceReview(currentUser.getUserId(), year);
                System.out.println("Creating new performance review for " + year);
            } else {
                System.out.println("Editing existing performance review for " + year);
            }

            System.out.println("\nKey Deliverables Achieved:");
            System.out.print("Enter (or press Enter to keep existing): ");
            String deliverables = scanner.nextLine();
            if (!deliverables.isEmpty()) review.setKeyDeliverables(deliverables);

            System.out.println("\nMajor Accomplishments:");
            System.out.print("Enter (or press Enter to keep existing): ");
            String accomplishments = scanner.nextLine();
            if (!accomplishments.isEmpty()) review.setMajorAccomplishments(accomplishments);

            System.out.println("\nAreas of Improvement:");
            System.out.print("Enter (or press Enter to keep existing): ");
            String improvements = scanner.nextLine();
            if (!improvements.isEmpty()) review.setAreasImprovement(improvements);

            System.out.println("\nSelf Assessment Rating (1-5):");
            System.out.print("Enter (or 0 to skip): ");
            double rating = scanner.nextDouble();
            scanner.nextLine();
            if (rating > 0 && InputValidator.isValidRating(rating)) {
                review.setSelfRating(rating);
            }

            if (performanceService.createOrUpdatePerformanceReview(review)) {
                System.out.println(" Performance review saved successfully!");
            } else {
                System.out.println(" Failed to save performance review.");
            }
        } catch (SQLException e) {
            System.err.println("Error saving performance review: " + e.getMessage());
        }
    }

    private void submitPerformanceReview() {
        System.out.println("\n=== Submit Performance Review ===");
        System.out.print("Enter review year to submit: ");
        int year = scanner.nextInt();
        scanner.nextLine();

        try {
            PerformanceReview review = performanceService.getPerformanceReview(currentUser.getUserId(), year);
            if (review == null) {
                System.out.println("No performance review found for " + year);
                return;
            }

            if (review.getStatus() == PerformanceReview.ReviewStatus.SUBMITTED) {
                System.out.println("Performance review already submitted.");
                return;
            }

            System.out.println("Review Summary:");
            System.out.println("Key Deliverables: " + (review.getKeyDeliverables() != null ? review.getKeyDeliverables() : "Not provided"));
            System.out.println("Major Accomplishments: " + (review.getMajorAccomplishments() != null ? review.getMajorAccomplishments() : "Not provided"));
            System.out.println("Areas of Improvement: " + (review.getAreasImprovement() != null ? review.getAreasImprovement() : "Not provided"));
            System.out.println("Self Rating: " + (review.getSelfRating() != null ? review.getSelfRating() : "Not provided"));

            System.out.print("\nAre you sure you want to submit this review? (yes/no): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("yes")) {
                if (performanceService.submitPerformanceReview(currentUser.getUserId(), year)) {
                    System.out.println(" Performance review submitted successfully!");

                    // Notify manager
                    User manager = userService.getReportingManager(currentUser.getUserId());
                    if (manager != null) {
                        notificationService.sendNotification(
                                manager.getUserId(),
                                "Performance Review Submitted",
                                currentUser.getFullName() + " has submitted their performance review for " + year,
                                Notification.NotificationType.PERFORMANCE
                        );
                    }
                } else {
                    System.out.println(" Failed to submit performance review.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error submitting review: " + e.getMessage());
        }
    }

    private void viewMyGoals() {
        System.out.println("\n=== My Goals ===");
        try {
            List<Goal> goals = performanceService.getMyGoals(currentUser.getUserId());
            if (goals.isEmpty()) {
                System.out.println("No goals set yet.");
            } else {
                for (Goal goal : goals) {
                    System.out.println("\n---");
                    System.out.println("Goal: " + goal.getGoalDescription());
                    System.out.println("Priority: " + goal.getPriority());
                    System.out.println("Deadline: " + (goal.getDeadline() != null ? dateFormat.format(goal.getDeadline()) : "Not set"));
                    System.out.println("Progress: " + goal.getProgressPercentage() + "%");
                    System.out.println("Status: " + goal.getStatus());
                    System.out.println("Success Metrics: " + (goal.getSuccessMetrics() != null ? goal.getSuccessMetrics() : "Not set"));
                    if (goal.getManagerFeedback() != null) {
                        System.out.println("Manager Feedback: " + goal.getManagerFeedback());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching goals: " + e.getMessage());
        }
    }

    private void addUpdateGoal() {
        System.out.println("\n=== Add/Update Goal ===");
        System.out.print("Goal Description: ");
        String description = scanner.nextLine();

        System.out.print("Deadline (YYYY-MM-DD, or press Enter to skip): ");
        String deadlineStr = scanner.nextLine();
        Date deadline = null;
        try {
            if (!deadlineStr.isEmpty()) {
                deadline = dateFormat.parse(deadlineStr);
            }
        } catch (ParseException e) {
            System.out.println("Invalid date format. Skipping deadline.");
        }

        System.out.println("Priority (HIGH/MEDIUM/LOW): ");
        String priorityStr = scanner.nextLine().toUpperCase();
        Goal.Priority priority = Goal.Priority.MEDIUM;
        try {
            priority = Goal.Priority.valueOf(priorityStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid priority. Using MEDIUM.");
        }

        System.out.print("Success Metrics: ");
        String metrics = scanner.nextLine();

        Goal goal = new Goal(currentUser.getUserId(), description, priority);
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
        System.out.println("\n=== Update Goal Progress ===");
        try {
            List<Goal> goals = performanceService.getMyGoals(currentUser.getUserId());
            if (goals.isEmpty()) {
                System.out.println("No goals to update.");
                return;
            }

            System.out.println("Your Goals:");
            for (Goal goal : goals) {
                System.out.printf("%d. %s (Current Progress: %d%%)\n",
                        goal.getGoalId(), goal.getGoalDescription(), goal.getProgressPercentage());
            }

            System.out.print("Enter Goal ID to update: ");
            int goalId = scanner.nextInt();
            System.out.print("Enter new progress percentage (0-100): ");
            int progress = scanner.nextInt();
            scanner.nextLine();

            if (InputValidator.isValidPercentage(progress)) {
                if (performanceService.updateGoalProgress(goalId, progress)) {
                    System.out.println(" Goal progress updated successfully!");
                } else {
                    System.out.println(" Failed to update goal progress.");
                }
            } else {
                System.out.println("Invalid progress percentage.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating goal: " + e.getMessage());
        }
    }

    private void viewManagerFeedback() {
        System.out.println("\n=== Manager Feedback ===");
        System.out.print("Enter review year to view feedback: ");
        int year = scanner.nextInt();
        scanner.nextLine();

        try {
            PerformanceReview review = performanceService.getPerformanceReview(currentUser.getUserId(), year);
            if (review != null && review.getManagerFeedback() != null) {
                System.out.println("\nManager's Feedback:");
                System.out.println("Rating: " + (review.getManagerRating() != null ? review.getManagerRating() : "Not rated yet"));
                System.out.println("Comments: " + review.getManagerFeedback());
                System.out.println("Reviewed on: " + (review.getReviewedDate() != null ? dateFormat.format(review.getReviewedDate()) : "Not reviewed"));
            } else {
                System.out.println("No feedback available for " + year);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching feedback: " + e.getMessage());
        }
    }

    private void viewHolidays() {
        try {
            holidayService.displayHolidayCalendar(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR));
        } catch (SQLException e) {
            System.err.println("Error fetching holidays: " + e.getMessage());
        }
    }

    private void viewAnnouncements() {
        System.out.println("\n=== Company Announcements ===");
        try {
            List<Announcement> announcements = announcementService.getActiveAnnouncements();
            if (announcements.isEmpty()) {
                System.out.println("No announcements at this time.");
            } else {
                for (Announcement announcement : announcements) {
                    System.out.println("\n----------------------------------------");
                    System.out.println("Title: " + announcement.getTitle());
                    System.out.println("Posted: " + announcement.getCreatedAt());
                    System.out.println("Content: " + announcement.getContent());
                    if (announcement.getExpiryDate() != null) {
                        System.out.println("Expires: " + dateFormat.format(announcement.getExpiryDate()));
                    }
                }
                System.out.println("----------------------------------------");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching announcements: " + e.getMessage());
        }
    }

    private void viewNotifications() {
        System.out.println("\n=== Notifications ===");
        try {
            List<Notification> notifications = notificationService.getAllNotifications(currentUser.getUserId());
            if (notifications.isEmpty()) {
                System.out.println("No notifications.");
            } else {
                for (Notification notification : notifications) {
                    System.out.printf("\n[%s] %s\n", notification.getType(), notification.getTitle());
                    System.out.println(notification.getMessage());
                    System.out.println("Received: " + notification.getCreatedAt());
                    if (!notification.isRead()) {
                        System.out.println(" UNREAD");
                        notificationService.markAsRead(notification.getNotificationId());
                    }
                    System.out.println("---");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching notifications: " + e.getMessage());
        }
    }

    private void viewEmployeeDirectory() {
        System.out.println("\n=== Employee Directory ===");
        try {
            List<User> employees = userService.getAllEmployees();
            if (employees.isEmpty()) {
                System.out.println("No employees found.");
            } else {
                System.out.printf("%-12s %-25s %-15s %-20s %-15s\n",
                        "Employee ID", "Name", "Department", "Designation", "Email");
                System.out.println("--------------------------------------------------------------------------------");
                for (User emp : employees) {
                    System.out.printf("%-12s %-25s %-15s %-20s %-15s\n",
                            emp.getEmployeeId(),
                            emp.getFullName(),
                            emp.getDepartment() != null ? emp.getDepartment() : "-",
                            emp.getDesignation() != null ? emp.getDesignation() : "-",
                            emp.getEmail());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employee directory: " + e.getMessage());
        }
    }

    private void changePassword() {
        AuthController authController = new AuthController();
        authController.changePassword(currentUser);
    }
}