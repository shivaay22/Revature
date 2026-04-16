package com.revworkforce.service;

import com.revworkforce.dao.NotificationDAO;
import com.revworkforce.model.Notification;

import java.sql.SQLException;
import java.util.List;

public class NotificationService {
    private NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    public List<Notification> getUnreadNotifications(int userId) throws SQLException {
        return notificationDAO.getUnreadNotifications(userId);
    }

    public List<Notification> getAllNotifications(int userId) throws SQLException {
        return notificationDAO.getAllNotifications(userId);
    }

    public int getUnreadCount(int userId) throws SQLException {
        return notificationDAO.getUnreadCount(userId);
    }

    public boolean markAsRead(int notificationId) throws SQLException {
        return notificationDAO.markAsRead(notificationId);
    }

    public boolean markAllAsRead(int userId) throws SQLException {
        return notificationDAO.markAllAsRead(userId);
    }

    public boolean sendNotification(int userId, String title, String message, Notification.NotificationType type) throws SQLException {
        Notification notification = new Notification(userId, title, message, type);
        return notificationDAO.createNotification(notification);
    }

    public void sendBirthdayNotifications(List<Integer> userIds) throws SQLException {
        for (int userId : userIds) {
            sendNotification(userId, "Happy Birthday!",
                    "Wishing you a very happy birthday! May your day be filled with joy and celebration.",
                    Notification.NotificationType.BIRTHDAY);
        }
    }

    public void sendAnniversaryNotifications(List<Integer> userIds) throws SQLException {
        for (int userId : userIds) {
            sendNotification(userId, "Work Anniversary",
                    "Congratulations on your work anniversary! Thank you for your continued dedication.",
                    Notification.NotificationType.ANNIVERSARY);
        }
    }
}