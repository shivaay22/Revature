package com.revworkforce.model;

import java.util.Date;

public class Goal {
    private int goalId;
    private int userId;
    private String goalDescription;
    private Date deadline;
    private Priority priority;
    private String successMetrics;
    private int progressPercentage;
    private GoalStatus status;
    private String managerFeedback;
    private Date createdAt;
    private Date updatedAt;

    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    public enum GoalStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    // Constructors
    public Goal() {}

    public Goal(int userId, String goalDescription, Priority priority) {
        this.userId = userId;
        this.goalDescription = goalDescription;
        this.priority = priority;
        this.progressPercentage = 0;
        this.status = GoalStatus.NOT_STARTED;
    }

    // Getters and Setters
    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getGoalDescription() {
        return goalDescription;
    }

    public void setGoalDescription(String goalDescription) {
        this.goalDescription = goalDescription;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getSuccessMetrics() {
        return successMetrics;
    }

    public void setSuccessMetrics(String successMetrics) {
        this.successMetrics = successMetrics;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    public String getManagerFeedback() {
        return managerFeedback;
    }

    public void setManagerFeedback(String managerFeedback) {
        this.managerFeedback = managerFeedback;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}