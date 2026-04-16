package com.revworkforce.dao;

import com.revworkforce.model.Holiday;
import com.revworkforce.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HolidayDAO {

    public List<Holiday> getHolidaysByYear(int year) throws SQLException {
        List<Holiday> holidays = new ArrayList<>();
        String query = "SELECT * FROM holidays WHERE year = ? ORDER BY holiday_date";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, year);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                holidays.add(mapResultSetToHoliday(rs));
            }
        }
        return holidays;
    }

    public boolean addHoliday(Holiday holiday) throws SQLException {
        String query = "INSERT INTO holidays (holiday_name, holiday_date, year, description) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, holiday.getHolidayName());
            pstmt.setDate(2, new java.sql.Date(holiday.getHolidayDate().getTime()));
            pstmt.setInt(3, holiday.getYear());
            pstmt.setString(4, holiday.getDescription());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteHoliday(int holidayId) throws SQLException {
        String query = "DELETE FROM holidays WHERE holiday_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, holidayId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean isHoliday(Date date) throws SQLException {
        String query = "SELECT COUNT(*) FROM holidays WHERE holiday_date = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDate(1, new java.sql.Date(date.getTime()));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private Holiday mapResultSetToHoliday(ResultSet rs) throws SQLException {
        Holiday holiday = new Holiday();
        holiday.setHolidayId(rs.getInt("holiday_id"));
        holiday.setHolidayName(rs.getString("holiday_name"));
        holiday.setHolidayDate(rs.getDate("holiday_date"));
        holiday.setYear(rs.getInt("year"));
        holiday.setDescription(rs.getString("description"));
        return holiday;
    }
}