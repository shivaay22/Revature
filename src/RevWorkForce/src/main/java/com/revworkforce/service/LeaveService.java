package com.revworkforce.service;

import com.revworkforce.dao.LeaveDAO;
import com.revworkforce.dao.NotificationDAO;
import com.revworkforce.model.LeaveBalance;
import com.revworkforce.model.LeaveRequest;
import com.revworkforce.model.Notification;
import com.revworkforce.model.User;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LeaveService {
    private LeaveDAO leaveDAO;
    private NotificationDAO notificationDAO;

    public LeaveService() {
        this.leaveDAO = new LeaveDAO();
        this.notificationDAO = new NotificationDAO();
    }

    public boolean applyLeave(int userId, LeaveRequest.LeaveType leaveType,
                              Date startDate, Date endDate, String reason) throws SQLException {

        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        int year = getYearFromDate(startDate);
        LeaveBalance balance = leaveDAO.getLeaveBalance(userId, leaveType, year);

        if (balance == null) {
            throw new IllegalArgumentException("Leave balance not configured for this year");
        }

        int requestedDays = calculateDays(startDate, endDate);

        if (balance.getAvailableDays() < requestedDays) {
            throw new IllegalArgumentException("Insufficient leave balance. Available: " +
                    balance.getAvailableDays() + ", Requested: " + requestedDays);
        }

        LeaveRequest request = new LeaveRequest(userId, leaveType, startDate, endDate, reason);
        return leaveDAO.applyLeave(request);
    }

    public List<LeaveRequest> getMyLeaveRequests(int userId) throws SQLException {
        return leaveDAO.getLeaveRequestsByUser(userId);
    }

    public List<LeaveRequest> getTeamLeaveRequests(int managerId) throws SQLException {
        return leaveDAO.getPendingLeaveRequestsByManager(managerId);
    }

    public List<LeaveRequest> getAllTeamLeaveRequests(int managerId) throws SQLException {
        return leaveDAO.getAllLeaveRequestsByManager(managerId);
    }

    public boolean approveLeave(int requestId, int managerId, String comments) throws SQLException {
        LeaveRequest request = getLeaveRequestById(requestId);
        if (request == null) {
            return false;
        }

        boolean updated = leaveDAO.updateLeaveStatus(requestId, "APPROVED", comments);

        if (updated) {
            int year = getYearFromDate(request.getStartDate());
            LeaveBalance balance = leaveDAO.getLeaveBalance(request.getUserId(), request.getLeaveType(), year);
            if (balance != null) {
                int newUsedDays = balance.getUsedDays() + request.getNumberOfDays();
                leaveDAO.updateLeaveBalance(request.getUserId(), request.getLeaveType(), year, newUsedDays);
            }

            Notification notification = new Notification(
                    request.getUserId(),
                    "Leave Approved",
                    "Your leave request from " + request.getStartDate() + " to " + request.getEndDate() +
                            " has been approved. Comments: " + comments,
                    Notification.NotificationType.LEAVE
            );
            notificationDAO.createNotification(notification);
        }

        return updated;
    }

    public boolean rejectLeave(int requestId, int managerId, String comments) throws SQLException {
        boolean updated = leaveDAO.updateLeaveStatus(requestId, "REJECTED", comments);

        if (updated) {
            LeaveRequest request = getLeaveRequestById(requestId);
            if (request != null) {
                Notification notification = new Notification(
                        request.getUserId(),
                        "Leave Rejected",
                        "Your leave request from " + request.getStartDate() + " to " + request.getEndDate() +
                                " has been rejected. Reason: " + comments,
                        Notification.NotificationType.LEAVE
                );
                notificationDAO.createNotification(notification);
            }
        }

        return updated;
    }

    public boolean cancelLeaveRequest(int requestId) throws SQLException {
        LeaveRequest request = getLeaveRequestById(requestId);
        if (request != null && request.getStatus() == LeaveRequest.LeaveStatus.PENDING) {
            return leaveDAO.cancelLeaveRequest(requestId);
        }
        return false;
    }

    public LeaveBalance getLeaveBalance(int userId, LeaveRequest.LeaveType leaveType, int year) throws SQLException {
        return leaveDAO.getLeaveBalance(userId, leaveType, year);
    }

    public boolean configureLeaveBalance(int userId, LeaveRequest.LeaveType leaveType, int totalDays, int year) throws SQLException {
        LeaveBalance existing = leaveDAO.getLeaveBalance(userId, leaveType, year);
        if (existing != null) {
            existing.setTotalDays(totalDays);
            return leaveDAO.updateLeaveBalance(userId, leaveType, year, existing.getUsedDays());
        } else {
            LeaveBalance balance = new LeaveBalance(userId, leaveType, totalDays, year);
            return leaveDAO.createLeaveBalance(balance);
        }
    }

    private LeaveRequest getLeaveRequestById(int requestId) throws SQLException {
        return null;
    }

    private int calculateDays(Date startDate, Date endDate) {
        long diff = endDate.getTime() - startDate.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24)) + 1;
    }

    private int getYearFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }
}