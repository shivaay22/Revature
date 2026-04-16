package com.revworkforce.service;

import com.revworkforce.dao.PerformanceDAO;
import com.revworkforce.dao.NotificationDAO;
import com.revworkforce.model.Goal;
import com.revworkforce.model.Notification;
import com.revworkforce.model.PerformanceReview;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class PerformanceService {
    private PerformanceDAO performanceDAO;
    private NotificationDAO notificationDAO;

    public PerformanceService() {
        this.performanceDAO = new PerformanceDAO();
        this.notificationDAO = new NotificationDAO();
    }

    // Performance Review Methods
    public boolean createOrUpdatePerformanceReview(PerformanceReview review) throws SQLException {
        PerformanceReview existing = performanceDAO.getPerformanceReview(review.getUserId(), review.getReviewYear());

        if (existing == null) {
            return performanceDAO.createPerformanceReview(review);
        } else {
            review.setReviewId(existing.getReviewId());
            return performanceDAO.updatePerformanceReview(review);
        }
    }

    public boolean submitPerformanceReview(int userId, int year) throws SQLException {
        PerformanceReview review = performanceDAO.getPerformanceReview(userId, year);
        if (review != null && review.getStatus() == PerformanceReview.ReviewStatus.DRAFT) {
            review.setStatus(PerformanceReview.ReviewStatus.SUBMITTED);
            review.setSubmittedDate(new Date());
            boolean updated = performanceDAO.updatePerformanceReview(review);

            if (updated) {
                // Notify manager
                // In production, get manager ID from user
                // This would need user service integration
                sendNotificationToManager(userId, "Performance Review Submitted",
                        "An employee has submitted their performance review for review.");
            }

            return updated;
        }
        return false;
    }

    public PerformanceReview getPerformanceReview(int userId, int year) throws SQLException {
        return performanceDAO.getPerformanceReview(userId, year);
    }

    public List<PerformanceReview> getTeamPerformanceReviews(int managerId) throws SQLException {
        return performanceDAO.getPerformanceReviewsByManager(managerId);
    }

    public boolean providePerformanceFeedback(int reviewId, double rating, String feedback) throws SQLException {
        boolean updated = performanceDAO.provideManagerFeedback(reviewId, rating, feedback);

        if (updated) {
            PerformanceReview review = performanceDAO.getPerformanceReview(reviewId, 0);
            if (review != null) {
                Notification notification = new Notification(
                        review.getUserId(),
                        "Performance Feedback Received",
                        "Your manager has provided feedback on your performance review. Rating: " + rating,
                        Notification.NotificationType.PERFORMANCE
                );
                notificationDAO.createNotification(notification);
            }
        }

        return updated;
    }

    // Goal Methods
    public boolean createGoal(Goal goal) throws SQLException {
        return performanceDAO.createGoal(goal);
    }

    public boolean updateGoal(Goal goal) throws SQLException {
        return performanceDAO.updateGoal(goal);
    }

    public List<Goal> getMyGoals(int userId) throws SQLException {
        return performanceDAO.getGoalsByUser(userId);
    }

    public List<Goal> getTeamGoals(int managerId) throws SQLException {
        return performanceDAO.getGoalsByManager(managerId);
    }

    public boolean updateGoalProgress(int goalId, int progress) throws SQLException {
        String status = progress == 100 ? "COMPLETED" : (progress > 0 ? "IN_PROGRESS" : "NOT_STARTED");
        return performanceDAO.updateGoalProgress(goalId, progress, status);
    }

    public boolean provideGoalFeedback(int goalId, String feedback) throws SQLException {
        boolean updated = performanceDAO.provideGoalFeedback(goalId, feedback);

        if (updated) {
            // Get goal to find user
            // For brevity, we'll skip getting user details
            // In production, get goal first to get user ID
        }

        return updated;
    }

    private void sendNotificationToManager(int employeeId, String title, String message) throws SQLException {
        // In production, get manager ID from employee
        // This would require UserService
        Notification notification = new Notification(
                0, // manager ID would go here
                title,
                message,
                Notification.NotificationType.PERFORMANCE
        );
        notificationDAO.createNotification(notification);
    }
}