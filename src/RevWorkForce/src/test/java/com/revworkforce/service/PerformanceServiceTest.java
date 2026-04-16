package com.revworkforce.service;

import com.revworkforce.dao.PerformanceDAO;
import com.revworkforce.dao.NotificationDAO;
import com.revworkforce.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerformanceServiceTest {

    @Mock
    private PerformanceDAO performanceDAO;

    @Mock
    private NotificationDAO notificationDAO;

    @InjectMocks
    @Spy
    private PerformanceService performanceService;

    private PerformanceReview review;

    @BeforeEach
    void setup() {
        review = new PerformanceReview();
        review.setUserId(1);
        review.setReviewYear(2026);
        review.setStatus(PerformanceReview.ReviewStatus.DRAFT);
    }


    // CREATE / UPDATE REVIEW


    @Test
    void testCreatePerformanceReview() throws SQLException {
        when(performanceDAO.getPerformanceReview(1, 2026)).thenReturn(null);
        when(performanceDAO.createPerformanceReview(review)).thenReturn(true);

        assertTrue(performanceService.createOrUpdatePerformanceReview(review));
    }

    @Test
    void testUpdatePerformanceReview() throws SQLException {
        PerformanceReview existing = new PerformanceReview();
        existing.setReviewId(10);

        when(performanceDAO.getPerformanceReview(1, 2026)).thenReturn(existing);
        when(performanceDAO.updatePerformanceReview(review)).thenReturn(true);

        boolean result = performanceService.createOrUpdatePerformanceReview(review);

        assertTrue(result);
        assertEquals(10, review.getReviewId());
    }


    // SUBMIT REVIEW


    @Test
    void testSubmitPerformanceReviewSuccess() throws SQLException {
        when(performanceDAO.getPerformanceReview(1, 2026)).thenReturn(review);
        when(performanceDAO.updatePerformanceReview(any())).thenReturn(true);

        boolean result = performanceService.submitPerformanceReview(1, 2026);

        assertTrue(result);
        verify(notificationDAO).createNotification(any(Notification.class));
    }

    @Test
    void testSubmitPerformanceReviewNotDraft() throws SQLException {
        review.setStatus(PerformanceReview.ReviewStatus.SUBMITTED);

        when(performanceDAO.getPerformanceReview(1, 2026)).thenReturn(review);

        assertFalse(performanceService.submitPerformanceReview(1, 2026));
    }

    @Test
    void testSubmitPerformanceReviewNotFound() throws SQLException {
        when(performanceDAO.getPerformanceReview(1, 2026)).thenReturn(null);

        assertFalse(performanceService.submitPerformanceReview(1, 2026));
    }


    // GET REVIEW


    @Test
    void testGetPerformanceReview() throws SQLException {
        when(performanceDAO.getPerformanceReview(1, 2026)).thenReturn(review);

        assertNotNull(performanceService.getPerformanceReview(1, 2026));
    }

    @Test
    void testGetTeamPerformanceReviews() throws SQLException {
        when(performanceDAO.getPerformanceReviewsByManager(10))
                .thenReturn(List.of(review));

        assertEquals(1, performanceService.getTeamPerformanceReviews(10).size());
    }


    // PROVIDE FEEDBACK


    @Test
    void testProvidePerformanceFeedbackSuccess() throws SQLException {
        when(performanceDAO.provideManagerFeedback(1, 4.5, "Good")).thenReturn(true);
        when(performanceDAO.getPerformanceReview(1, 0)).thenReturn(review);

        boolean result = performanceService.providePerformanceFeedback(1, 4.5, "Good");

        assertTrue(result);
        verify(notificationDAO).createNotification(any(Notification.class));
    }

    @Test
    void testProvidePerformanceFeedbackReviewNotFound() throws SQLException {
        when(performanceDAO.provideManagerFeedback(1, 4.5, "Good")).thenReturn(true);
        when(performanceDAO.getPerformanceReview(1, 0)).thenReturn(null);

        boolean result = performanceService.providePerformanceFeedback(1, 4.5, "Good");

        assertTrue(result); // update success but no notification
        verify(notificationDAO, never()).createNotification(any());
    }

    @Test
    void testProvidePerformanceFeedbackFail() throws SQLException {
        when(performanceDAO.provideManagerFeedback(1, 4.5, "Good")).thenReturn(false);

        assertFalse(performanceService.providePerformanceFeedback(1, 4.5, "Good"));
    }


    // GOALS


    @Test
    void testCreateGoal() throws SQLException {
        Goal goal = new Goal();

        when(performanceDAO.createGoal(goal)).thenReturn(true);

        assertTrue(performanceService.createGoal(goal));
    }

    @Test
    void testUpdateGoal() throws SQLException {
        Goal goal = new Goal();

        when(performanceDAO.updateGoal(goal)).thenReturn(true);

        assertTrue(performanceService.updateGoal(goal));
    }

    @Test
    void testGetMyGoals() throws SQLException {
        when(performanceDAO.getGoalsByUser(1))
                .thenReturn(List.of(new Goal()));

        assertEquals(1, performanceService.getMyGoals(1).size());
    }

    @Test
    void testGetTeamGoals() throws SQLException {
        when(performanceDAO.getGoalsByManager(10))
                .thenReturn(List.of(new Goal()));

        assertEquals(1, performanceService.getTeamGoals(10).size());
    }


    // GOAL PROGRESS


    @Test
    void testUpdateGoalProgressCompleted() throws SQLException {
        when(performanceDAO.updateGoalProgress(1, 100, "COMPLETED"))
                .thenReturn(true);

        assertTrue(performanceService.updateGoalProgress(1, 100));
    }

    @Test
    void testUpdateGoalProgressInProgress() throws SQLException {
        when(performanceDAO.updateGoalProgress(1, 50, "IN_PROGRESS"))
                .thenReturn(true);

        assertTrue(performanceService.updateGoalProgress(1, 50));
    }

    @Test
    void testUpdateGoalProgressNotStarted() throws SQLException {
        when(performanceDAO.updateGoalProgress(1, 0, "NOT_STARTED"))
                .thenReturn(true);

        assertTrue(performanceService.updateGoalProgress(1, 0));
    }


    // GOAL FEEDBACK


    @Test
    void testProvideGoalFeedbackSuccess() throws SQLException {
        when(performanceDAO.provideGoalFeedback(1, "Good")).thenReturn(true);

        assertTrue(performanceService.provideGoalFeedback(1, "Good"));
    }

    @Test
    void testProvideGoalFeedbackFail() throws SQLException {
        when(performanceDAO.provideGoalFeedback(1, "Good")).thenReturn(false);

        assertFalse(performanceService.provideGoalFeedback(1, "Good"));
    }
}