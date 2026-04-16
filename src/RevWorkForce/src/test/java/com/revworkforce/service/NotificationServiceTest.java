package com.revworkforce.service;

import com.revworkforce.dao.NotificationDAO;
import com.revworkforce.model.Notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationDAO notificationDAO;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;

    @BeforeEach
    void setup() {
        notification = new Notification(
                1,
                "Test",
                "Test Message",
                Notification.NotificationType.LEAVE
        );
    }


    // GET NOTIFICATIONS


    @Test
    void testGetUnreadNotifications() throws SQLException {
        when(notificationDAO.getUnreadNotifications(1))
                .thenReturn(List.of(notification));

        List<Notification> result = notificationService.getUnreadNotifications(1);

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllNotifications() throws SQLException {
        when(notificationDAO.getAllNotifications(1))
                .thenReturn(List.of(notification));

        List<Notification> result = notificationService.getAllNotifications(1);

        assertEquals(1, result.size());
    }

    @Test
    void testGetUnreadCount() throws SQLException {
        when(notificationDAO.getUnreadCount(1)).thenReturn(5);

        int count = notificationService.getUnreadCount(1);

        assertEquals(5, count);
    }


    // MARK AS READ


    @Test
    void testMarkAsReadSuccess() throws SQLException {
        when(notificationDAO.markAsRead(1)).thenReturn(true);

        assertTrue(notificationService.markAsRead(1));
    }

    @Test
    void testMarkAsReadFail() throws SQLException {
        when(notificationDAO.markAsRead(1)).thenReturn(false);

        assertFalse(notificationService.markAsRead(1));
    }

    @Test
    void testMarkAllAsRead() throws SQLException {
        when(notificationDAO.markAllAsRead(1)).thenReturn(true);

        assertTrue(notificationService.markAllAsRead(1));
    }


    // SEND NOTIFICATION


    @Test
    void testSendNotificationSuccess() throws SQLException {
        when(notificationDAO.createNotification(any(Notification.class)))
                .thenReturn(true);

        boolean result = notificationService.sendNotification(
                1,
                "Hello",
                "Message",
                Notification.NotificationType.BIRTHDAY
        );

        assertTrue(result);
        verify(notificationDAO).createNotification(any(Notification.class));
    }

    @Test
    void testSendNotificationFail() throws SQLException {
        when(notificationDAO.createNotification(any(Notification.class)))
                .thenReturn(false);

        boolean result = notificationService.sendNotification(
                1,
                "Hello",
                "Message",
                Notification.NotificationType.ANNIVERSARY
        );

        assertFalse(result);
    }


    // BIRTHDAY NOTIFICATIONS


    @Test
    void testSendBirthdayNotifications() throws SQLException {
        when(notificationDAO.createNotification(any()))
                .thenReturn(true);

        List<Integer> users = List.of(1, 2, 3);

        notificationService.sendBirthdayNotifications(users);

        verify(notificationDAO, times(3))
                .createNotification(any(Notification.class));
    }

    @Test
    void testSendBirthdayNotificationsEmptyList() throws SQLException {
        notificationService.sendBirthdayNotifications(List.of());

        verify(notificationDAO, never())
                .createNotification(any());
    }


    // ANNIVERSARY NOTIFICATIONS


    @Test
    void testSendAnniversaryNotifications() throws SQLException {
        when(notificationDAO.createNotification(any()))
                .thenReturn(true);

        List<Integer> users = List.of(1, 2);

        notificationService.sendAnniversaryNotifications(users);

        verify(notificationDAO, times(2))
                .createNotification(any(Notification.class));
    }

    @Test
    void testSendAnniversaryNotificationsEmpty() throws SQLException {
        notificationService.sendAnniversaryNotifications(List.of());

        verify(notificationDAO, never())
                .createNotification(any());
    }
}