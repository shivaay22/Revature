package com.revworkforce.model;

import java.util.Date;

public class PerformanceReview {
    private int reviewId;
    private int userId;
    private int reviewYear;
    private String keyDeliverables;
    private String majorAccomplishments;
    private String areasImprovement;
    private Double selfRating;
    private Double managerRating;
    private String managerFeedback;
    private ReviewStatus status;
    private Date submittedDate;
    private Date reviewedDate;

    public enum ReviewStatus {
        DRAFT, SUBMITTED, REVIEWED
    }

    // Constructors
    public PerformanceReview() {}

    public PerformanceReview(int userId, int reviewYear) {
        this.userId = userId;
        this.reviewYear = reviewYear;
        this.status = ReviewStatus.DRAFT;
    }

    // Getters and Setters
    public int getReviewId() {
        return reviewId;
    }
    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getReviewYear() {
        return reviewYear;
    }

    public void setReviewYear(int reviewYear) {
        this.reviewYear = reviewYear;
    }

    public String getKeyDeliverables() {
        return keyDeliverables;
    }

    public void setKeyDeliverables(String keyDeliverables) {
        this.keyDeliverables = keyDeliverables;
    }

    public String getMajorAccomplishments() {
        return majorAccomplishments;
    }

    public void setMajorAccomplishments(String majorAccomplishments) {
        this.majorAccomplishments = majorAccomplishments;
    }

    public String getAreasImprovement() {
        return areasImprovement;
    }

    public void setAreasImprovement(String areasImprovement) {
        this.areasImprovement = areasImprovement;
    }

    public Double getSelfRating() {
        return selfRating;
    }

    public void setSelfRating(Double selfRating) {
        this.selfRating = selfRating;
    }

    public Double getManagerRating() {
        return managerRating;
    }

    public void setManagerRating(Double managerRating) {
        this.managerRating = managerRating;
    }

    public String getManagerFeedback() {
        return managerFeedback;
    }

    public void setManagerFeedback(String managerFeedback) {
        this.managerFeedback = managerFeedback;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public Date getReviewedDate() {
        return reviewedDate;
    }

    public void setReviewedDate(Date reviewedDate) {
        this.reviewedDate = reviewedDate;
    }
}