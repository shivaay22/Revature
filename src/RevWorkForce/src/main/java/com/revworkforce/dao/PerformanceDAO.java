package com.revworkforce.dao;

import com.revworkforce.model.Goal;
import com.revworkforce.model.PerformanceReview;
import com.revworkforce.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerformanceDAO {

    public boolean createPerformanceReview(PerformanceReview review) throws SQLException {
        String query = "INSERT INTO performance_reviews (user_id, review_year, key_deliverables, " +
                "major_accomplishments, areas_improvement, self_rating, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, review.getUserId());
            pstmt.setInt(2, review.getReviewYear());
            pstmt.setString(3, review.getKeyDeliverables());
            pstmt.setString(4, review.getMajorAccomplishments());
            pstmt.setString(5, review.getAreasImprovement());
            pstmt.setObject(6, review.getSelfRating());
            pstmt.setString(7, review.getStatus().toString());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    review.setReviewId(generatedKeys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public boolean updatePerformanceReview(PerformanceReview review) throws SQLException {
        String query = "UPDATE performance_reviews SET key_deliverables = ?, major_accomplishments = ?, " +
                "areas_improvement = ?, self_rating = ?, status = ?, submitted_date = ? " +
                "WHERE review_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, review.getKeyDeliverables());
            pstmt.setString(2, review.getMajorAccomplishments());
            pstmt.setString(3, review.getAreasImprovement());
            pstmt.setObject(4, review.getSelfRating());
            pstmt.setString(5, review.getStatus().toString());
            pstmt.setDate(6, review.getSubmittedDate() != null ? new java.sql.Date(review.getSubmittedDate().getTime()) : null);
            pstmt.setInt(7, review.getReviewId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public PerformanceReview getPerformanceReview(int userId, int year) throws SQLException {
        String query = "SELECT * FROM performance_reviews WHERE user_id = ? AND review_year = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, year);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPerformanceReview(rs);
            }
        }
        return null;
    }

    public List<PerformanceReview> getPerformanceReviewsByManager(int managerId) throws SQLException {
        List<PerformanceReview> reviews = new ArrayList<>();
        String query = "SELECT pr.* FROM performance_reviews pr " +
                "INNER JOIN users u ON pr.user_id = u.user_id " +
                "WHERE u.manager_id = ? AND pr.status = 'SUBMITTED'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, managerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reviews.add(mapResultSetToPerformanceReview(rs));
            }
        }
        return reviews;
    }

    public boolean provideManagerFeedback(int reviewId, double managerRating, String feedback) throws SQLException {
        String query = "UPDATE performance_reviews SET manager_rating = ?, manager_feedback = ?, " +
                "status = 'REVIEWED', reviewed_date = ? WHERE review_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDouble(1, managerRating);
            pstmt.setString(2, feedback);
            pstmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            pstmt.setInt(4, reviewId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean createGoal(Goal goal) throws SQLException {
        String query = "INSERT INTO goals (user_id, goal_description, deadline, priority, success_metrics, " +
                "progress_percentage, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, goal.getUserId());
            pstmt.setString(2, goal.getGoalDescription());
            pstmt.setDate(3, goal.getDeadline() != null ? new java.sql.Date(goal.getDeadline().getTime()) : null);
            pstmt.setString(4, goal.getPriority().toString());
            pstmt.setString(5, goal.getSuccessMetrics());
            pstmt.setInt(6, goal.getProgressPercentage());
            pstmt.setString(7, goal.getStatus().toString());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    goal.setGoalId(generatedKeys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateGoal(Goal goal) throws SQLException {
        String query = "UPDATE goals SET goal_description = ?, deadline = ?, priority = ?, " +
                "success_metrics = ?, progress_percentage = ?, status = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE goal_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, goal.getGoalDescription());
            pstmt.setDate(2, goal.getDeadline() != null ? new java.sql.Date(goal.getDeadline().getTime()) : null);
            pstmt.setString(3, goal.getPriority().toString());
            pstmt.setString(4, goal.getSuccessMetrics());
            pstmt.setInt(5, goal.getProgressPercentage());
            pstmt.setString(6, goal.getStatus().toString());
            pstmt.setInt(7, goal.getGoalId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Goal> getGoalsByUser(int userId) throws SQLException {
        List<Goal> goals = new ArrayList<>();
        String query = "SELECT * FROM goals WHERE user_id = ? ORDER BY priority DESC, deadline ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                goals.add(mapResultSetToGoal(rs));
            }
        }
        return goals;
    }

    public List<Goal> getGoalsByManager(int managerId) throws SQLException {
        List<Goal> goals = new ArrayList<>();
        String query = "SELECT g.* FROM goals g " +
                "INNER JOIN users u ON g.user_id = u.user_id " +
                "WHERE u.manager_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, managerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                goals.add(mapResultSetToGoal(rs));
            }
        }
        return goals;
    }

    public boolean updateGoalProgress(int goalId, int progress, String status) throws SQLException {
        String query = "UPDATE goals SET progress_percentage = ?, status = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE goal_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, progress);
            pstmt.setString(2, status);
            pstmt.setInt(3, goalId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean provideGoalFeedback(int goalId, String feedback) throws SQLException {
        String query = "UPDATE goals SET manager_feedback = ? WHERE goal_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, feedback);
            pstmt.setInt(2, goalId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public PerformanceReview getPerformanceReviewById(int reviewId) throws SQLException {
        String query = "SELECT * FROM performance_reviews WHERE review_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reviewId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPerformanceReview(rs);
                }
            }
        }
        return null;
    }

    private PerformanceReview mapResultSetToPerformanceReview(ResultSet rs) throws SQLException {
        PerformanceReview review = new PerformanceReview();
        review.setReviewId(rs.getInt("review_id"));
        review.setUserId(rs.getInt("user_id"));
        review.setReviewYear(rs.getInt("review_year"));
        review.setKeyDeliverables(rs.getString("key_deliverables"));
        review.setMajorAccomplishments(rs.getString("major_accomplishments"));
        review.setAreasImprovement(rs.getString("areas_improvement"));

        double selfRating = rs.getDouble("self_rating");
        if (!rs.wasNull()) review.setSelfRating(selfRating);

        double managerRating = rs.getDouble("manager_rating");
        if (!rs.wasNull()) review.setManagerRating(managerRating);

        review.setManagerFeedback(rs.getString("manager_feedback"));
        review.setStatus(PerformanceReview.ReviewStatus.valueOf(rs.getString("status")));
        review.setSubmittedDate(rs.getDate("submitted_date"));
        review.setReviewedDate(rs.getDate("reviewed_date"));
        return review;
    }

    private Goal mapResultSetToGoal(ResultSet rs) throws SQLException {
        Goal goal = new Goal();
        goal.setGoalId(rs.getInt("goal_id"));
        goal.setUserId(rs.getInt("user_id"));
        goal.setGoalDescription(rs.getString("goal_description"));
        goal.setDeadline(rs.getDate("deadline"));
        goal.setPriority(Goal.Priority.valueOf(rs.getString("priority")));
        goal.setSuccessMetrics(rs.getString("success_metrics"));
        goal.setProgressPercentage(rs.getInt("progress_percentage"));
        goal.setStatus(Goal.GoalStatus.valueOf(rs.getString("status")));
        goal.setManagerFeedback(rs.getString("manager_feedback"));
        goal.setCreatedAt(rs.getTimestamp("created_at"));
        goal.setUpdatedAt(rs.getTimestamp("updated_at"));
        return goal;
    }
}