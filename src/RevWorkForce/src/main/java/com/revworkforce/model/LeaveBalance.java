package com.revworkforce.model;

public class LeaveBalance {
    private int balanceId;
    private int userId;
    private LeaveRequest.LeaveType leaveType;
    private int totalDays;
    private int usedDays;
    private int year;

    public LeaveBalance() {}

    public LeaveBalance(int userId, LeaveRequest.LeaveType leaveType, int totalDays, int year) {
        this.userId = userId;
        this.leaveType = leaveType;
        this.totalDays = totalDays;
        this.usedDays = 0;
        this.year = year;
    }

    public int getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(int balanceId) {
        this.balanceId = balanceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LeaveRequest.LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveRequest.LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public int getUsedDays() {
        return usedDays;
    }

    public void setUsedDays(int usedDays) {
        this.usedDays = usedDays;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getAvailableDays() {
        return totalDays - usedDays;
    }
}