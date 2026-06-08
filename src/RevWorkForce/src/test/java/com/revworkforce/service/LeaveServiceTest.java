package com.revworkforce.service;

import com.revworkforce.dao.LeaveDAO;
import com.revworkforce.dao.NotificationDAO;
import com.revworkforce.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock
    private LeaveDAO leaveDAO;

    @Mock
    private NotificationDAO notificationDAO;

    @InjectMocks
    private LeaveService leaveService;

    private Date startDate;
    private Date endDate;

    @BeforeEach
    void setup() {
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.JANUARY, 1);
        startDate = cal.getTime();

        cal.set(2026, Calendar.JANUARY, 3);
        endDate = cal.getTime();
    }

    // APPLY LEAVE


    @Test
    void testApplyLeaveSuccess() throws SQLException {
        LeaveBalance balance = new LeaveBalance(1, LeaveRequest.LeaveType.SICK, 10, 2026);
        balance.setUsedDays(2);

        when(leaveDAO.getLeaveBalance(1, LeaveRequest.LeaveType.SICK, 2026))
                .thenReturn(balance);

        when(leaveDAO.applyLeave(any())).thenReturn(true);

        boolean result = leaveService.applyLeave(
                1,
                LeaveRequest.LeaveType.SICK,
                startDate,
                endDate,
                "Fever"
        );

        assertTrue(result);
    }

    @Test
    void testApplyLeaveInvalidDate() {
        assertThrows(IllegalArgumentException.class, () ->
            leaveService.applyLeave(1,
                    LeaveRequest.LeaveType.SICK,
                    endDate,
                    startDate,
                    "Invalid"));
    }

    @Test
    void testApplyLeaveInsufficientBalance() throws SQLException {
        LeaveBalance balance = new LeaveBalance(1, LeaveRequest.LeaveType.SICK, 2, 2026);
        balance.setUsedDays(0);

        when(leaveDAO.getLeaveBalance(anyInt(), any(), anyInt()))
                .thenReturn(balance);

        assertThrows(IllegalArgumentException.class, () ->
            leaveService.applyLeave(1,
                    LeaveRequest.LeaveType.SICK,
                    startDate,
                    endDate,
                    "Test"));
    }

    // GET LEAVE REQUESTS

    @Test
    void testGetMyLeaveRequests() throws SQLException {
        when(leaveDAO.getLeaveRequestsByUser(1))
                .thenReturn(List.of(new LeaveRequest()));

        List<LeaveRequest> result = leaveService.getMyLeaveRequests(1);

        assertEquals(1, result.size());
    }

    @Test
    void testGetTeamLeaveRequests() throws SQLException {
        when(leaveDAO.getPendingLeaveRequestsByManager(10))
                .thenReturn(List.of(new LeaveRequest()));

        assertEquals(1, leaveService.getTeamLeaveRequests(10).size());
    }

    @Test
    void testGetAllTeamLeaveRequests() throws SQLException {
        when(leaveDAO.getAllLeaveRequestsByManager(10))
                .thenReturn(List.of(new LeaveRequest()));

        assertEquals(1, leaveService.getAllTeamLeaveRequests(10).size());
    }


    // APPROVE LEAVE


    @Test
    void testApproveLeaveSuccess() throws SQLException {
        LeaveRequest request = mock(LeaveRequest.class);

        when(request.getUserId()).thenReturn(1);
        when(request.getStartDate()).thenReturn(startDate);
        when(request.getEndDate()).thenReturn(endDate);
        when(request.getLeaveType()).thenReturn(LeaveRequest.LeaveType.SICK);
        when(request.getNumberOfDays()).thenReturn(3);

        when(leaveDAO.getLeaveRequestById(1)).thenReturn(request);

        LeaveBalance balance = new LeaveBalance(1, LeaveRequest.LeaveType.SICK, 10, 2026);
        balance.setUsedDays(2);

        when(leaveDAO.updateLeaveStatus(1, "APPROVED", "OK")).thenReturn(true);
        when(leaveDAO.getLeaveBalance(anyInt(), any(), anyInt())).thenReturn(balance);

        boolean result = leaveService.approveLeave(1, 10, "OK");

        assertTrue(result);
        verify(notificationDAO).createNotification(any(Notification.class));
    }

    @Test
    void testApproveLeaveNotFound() throws SQLException {
        when(leaveDAO.getLeaveRequestById(1)).thenReturn(null);

        boolean result = leaveService.approveLeave(1, 10, "OK");

        assertFalse(result);
    }

    // REJECT LEAVE


    @Test
    void testRejectLeaveSuccess() throws SQLException {
        LeaveRequest request = new LeaveRequest();
        request.setUserId(1);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        when(leaveDAO.getLeaveRequestById(1)).thenReturn(request);
        when(leaveDAO.updateLeaveStatus(1, "REJECTED", "No")).thenReturn(true);

        boolean result = leaveService.rejectLeave(1, 10, "No");

        assertTrue(result);
        verify(notificationDAO).createNotification(any(Notification.class));
    }


    // CANCEL LEAVE


    @Test
    void testCancelLeaveSuccess() throws SQLException {
        LeaveRequest request = mock(LeaveRequest.class);

        when(request.getStatus()).thenReturn(LeaveRequest.LeaveStatus.PENDING);

        when(leaveDAO.getLeaveRequestById(1)).thenReturn(request);
        when(leaveDAO.cancelLeaveRequest(1)).thenReturn(true);

        assertTrue(leaveService.cancelLeaveRequest(1));
    }

    @Test
    void testCancelLeaveFail() throws SQLException {
        LeaveRequest request = mock(LeaveRequest.class);

        when(request.getStatus()).thenReturn(LeaveRequest.LeaveStatus.APPROVED);

        when(leaveDAO.getLeaveRequestById(1)).thenReturn(request);

        assertFalse(leaveService.cancelLeaveRequest(1));
    }


    // GET BALANCE


    @Test
    void testGetLeaveBalance() throws SQLException {
        LeaveBalance balance = new LeaveBalance();

        when(leaveDAO.getLeaveBalance(1, LeaveRequest.LeaveType.SICK, 2026))
                .thenReturn(balance);

        assertNotNull(leaveService.getLeaveBalance(1,
                LeaveRequest.LeaveType.SICK, 2026));
    }


    // CONFIGURE BALANCE


    @Test
    void testConfigureLeaveBalanceUpdate() throws SQLException {
        LeaveBalance balance = new LeaveBalance(1, LeaveRequest.LeaveType.SICK, 10, 2026);
        balance.setUsedDays(2);

        when(leaveDAO.getLeaveBalance(anyInt(), any(), anyInt()))
                .thenReturn(balance);

        when(leaveDAO.updateLeaveBalance(anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(true);

        assertTrue(leaveService.configureLeaveBalance(
                1,
                LeaveRequest.LeaveType.SICK,
                15,
                2026));
    }

    @Test
    void testConfigureLeaveBalanceCreate() throws SQLException {
        when(leaveDAO.getLeaveBalance(anyInt(), any(), anyInt()))
                .thenReturn(null);

        when(leaveDAO.createLeaveBalance(any()))
                .thenReturn(true);

        assertTrue(leaveService.configureLeaveBalance(
                1,
                LeaveRequest.LeaveType.SICK,
                10,
                2026));
    }
}