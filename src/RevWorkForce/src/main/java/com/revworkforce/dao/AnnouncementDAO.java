package com.revworkforce.dao;

import com.revworkforce.model.Announcement;
import com.revworkforce.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAO {

    public List<Announcement> getActiveAnnouncements() throws SQLException {
        List<Announcement> announcements = new ArrayList<>();
        String query = "SELECT * FROM announcements WHERE expiry_date IS NULL OR expiry_date >= CURDATE() " +
                "ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                announcements.add(mapResultSetToAnnouncement(rs));
            }
        }
        return announcements;
    }

    public boolean createAnnouncement(Announcement announcement) throws SQLException {
        String query = "INSERT INTO announcements (title, content, created_by, expiry_date) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, announcement.getTitle());
            pstmt.setString(2, announcement.getContent());
            pstmt.setInt(3, announcement.getCreatedBy());
            pstmt.setDate(4, announcement.getExpiryDate() != null ? new java.sql.Date(announcement.getExpiryDate().getTime()) : null);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    announcement.setAnnouncementId(generatedKeys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public boolean deleteAnnouncement(int announcementId) throws SQLException {
        String query = "DELETE FROM announcements WHERE announcement_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, announcementId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Announcement mapResultSetToAnnouncement(ResultSet rs) throws SQLException {
        Announcement announcement = new Announcement();
        announcement.setAnnouncementId(rs.getInt("announcement_id"));
        announcement.setTitle(rs.getString("title"));
        announcement.setContent(rs.getString("content"));
        announcement.setCreatedBy(rs.getInt("created_by"));
        announcement.setCreatedAt(rs.getTimestamp("created_at"));
        announcement.setExpiryDate(rs.getDate("expiry_date"));
        return announcement;
    }
}