package com.revworkforce.dao;

import com.revworkforce.model.LeaveBalance;
import com.revworkforce.model.LeaveRequest;
import com.revworkforce.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {

    public boolean applyLeave(LeaveRequest leaveRequest) throws SQLException {
        String query = "INSERT INTO leave_requests (user_id, leave_type, start_date, end_date, reason, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, leaveRequest.getUserId());
            pstmt.setString(2, leaveRequest.getLeaveType().toString());
            pstmt.setDate(3, new java.sql.Date(leaveRequest.getStartDate().getTime()));
            pstmt.setDate(4, new java.sql.Date(leaveRequest.getEndDate().getTime()));
            pstmt.setString(5, leaveRequest.getReason());
            pstmt.setString(6, leaveRequest.getStatus().toString());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    leaveRequest.setRequestId(generatedKeys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public List<LeaveRequest> getLeaveRequestsByUser(int userId) throws SQLException {
        List<LeaveRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM leave_requests WHERE user_id = ? ORDER BY applied_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                requests.add(mapResultSetToLeaveRequest(rs));
            }
        }
        return requests;
    }

    public List<LeaveRequest> getPendingLeaveRequestsByManager(int managerId) throws SQLException {
        List<LeaveRequest> requests = new ArrayList<>();
        String query = "SELECT lr.* FROM leave_requests lr " +
                "INNER JOIN users u ON lr.user_id = u.user_id " +
                "WHERE u.manager_id = ? AND lr.status = 'PENDING' " +
                "ORDER BY lr.applied_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, managerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                requests.add(mapResultSetToLeaveRequest(rs));
            }
        }
        return requests;
    }

    public List<LeaveRequest> getAllLeaveRequestsByManager(int managerId) throws SQLException {
        List<LeaveRequest> requests = new ArrayList<>();
        String query = "SELECT lr.* FROM leave_requests lr " +
                "INNER JOIN users u ON lr.user_id = u.user_id " +
                "WHERE u.manager_id = ? ORDER BY lr.applied_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, managerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                requests.add(mapResultSetToLeaveRequest(rs));
            }
        }
        return requests;
    }

    public boolean updateLeaveStatus(int requestId, String status, String comments) throws SQLException {
        String query = "UPDATE leave_requests SET status = ?, manager_comments = ?, reviewed_date = ? WHERE request_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status);
            pstmt.setString(2, comments);
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(4, requestId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean cancelLeaveRequest(int requestId) throws SQLException {
        String query = "UPDATE leave_requests SET status = 'CANCELLED' WHERE request_id = ? AND status = 'PENDING'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, requestId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public LeaveBalance getLeaveBalance(int userId, LeaveRequest.LeaveType leaveType, int year) throws SQLException {
        String query = "SELECT * FROM leave_balances WHERE user_id = ? AND leave_type = ? AND year = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, leaveType.toString());
            pstmt.setInt(3, year);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                LeaveBalance balance = new LeaveBalance();
                balance.setBalanceId(rs.getInt("balance_id"));
                balance.setUserId(rs.getInt("user_id"));
                balance.setLeaveType(LeaveRequest.LeaveType.valueOf(rs.getString("leave_type")));
                balance.setTotalDays(rs.getInt("total_days"));
                balance.setUsedDays(rs.getInt("used_days"));
                balance.setYear(rs.getInt("year"));
                return balance;
            }
        }
        return null;
    }

    public boolean updateLeaveBalance(int userId, LeaveRequest.LeaveType leaveType, int year, int usedDays) throws SQLException {
        String query = "UPDATE leave_balances SET used_days = ? WHERE user_id = ? AND leave_type = ? AND year = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, usedDays);
            pstmt.setInt(2, userId);
            pstmt.setString(3, leaveType.toString());
            pstmt.setInt(4, year);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean createLeaveBalance(LeaveBalance balance) throws SQLException {
        String query = "INSERT INTO leave_balances (user_id, leave_type, total_days, used_days, year) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, balance.getUserId());
            pstmt.setString(2, balance.getLeaveType().toString());
            pstmt.setInt(3, balance.getTotalDays());
            pstmt.setInt(4, balance.getUsedDays());
            pstmt.setInt(5, balance.getYear());

            return pstmt.executeUpdate() > 0;
        }
    }

    private LeaveRequest mapResultSetToLeaveRequest(ResultSet rs) throws SQLException {
        LeaveRequest request = new LeaveRequest();
        request.setRequestId(rs.getInt("request_id"));
        request.setUserId(rs.getInt("user_id"));
        request.setLeaveType(LeaveRequest.LeaveType.valueOf(rs.getString("leave_type")));
        request.setStartDate(rs.getDate("start_date"));
        request.setEndDate(rs.getDate("end_date"));
        request.setReason(rs.getString("reason"));
        request.setStatus(LeaveRequest.LeaveStatus.valueOf(rs.getString("status")));
        request.setManagerComments(rs.getString("manager_comments"));
        request.setAppliedDate(rs.getTimestamp("applied_date"));
        request.setReviewedDate(rs.getTimestamp("reviewed_date"));
        return request;
    }
}