package com.revworkforce.service;

import com.revworkforce.dao.AnnouncementDAO;
import com.revworkforce.model.Announcement;
import java.sql.SQLException;
import java.util.List;

public class AnnouncementService {
    private AnnouncementDAO announcementDAO;

    public AnnouncementService() {
        this.announcementDAO = new AnnouncementDAO();
    }

    public List<Announcement> getActiveAnnouncements() throws SQLException {
        return announcementDAO.getActiveAnnouncements();
    }

    public boolean createAnnouncement(Announcement announcement) throws SQLException {
        if (announcement == null) {
            throw new IllegalArgumentException("Announcement cannot be null");
        }
        if (announcement.getTitle() == null || announcement.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (announcement.getContent() == null || announcement.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
        return announcementDAO.createAnnouncement(announcement);
    }

    public boolean deleteAnnouncement(int announcementId) throws SQLException {
        return announcementDAO.deleteAnnouncement(announcementId);
    }
}
