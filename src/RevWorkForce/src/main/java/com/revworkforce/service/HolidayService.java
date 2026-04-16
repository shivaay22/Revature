package com.revworkforce.service;

import com.revworkforce.dao.HolidayDAO;
import com.revworkforce.exceptions.ValidationException;
import com.revworkforce.model.Holiday;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HolidayService {
    private HolidayDAO holidayDAO;

    public HolidayService() {
        this.holidayDAO = new HolidayDAO();
    }

    public List<Holiday> getHolidaysForYear(int year) throws SQLException {
        return holidayDAO.getHolidaysByYear(year);
    }

    public List<Holiday> getCurrentYearHolidays() throws SQLException {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return holidayDAO.getHolidaysByYear(currentYear);
    }

    public boolean addHoliday(Holiday holiday) throws SQLException {
        if (holiday == null) {
            throw new ValidationException("Holiday cannot be null");
        }

        if (holiday.getHolidayDate() == null) {
            throw new ValidationException("Holiday date cannot be null");
        }

        if (holiday.getHolidayDate().before(new Date())) {
            throw new ValidationException("Holiday date cannot be in the past");
        }

        if (holiday.getHolidayName() == null || holiday.getHolidayName().trim().isEmpty()) {
            throw new ValidationException("Holiday name cannot be empty");
        }
        return holidayDAO.addHoliday(holiday);
    }

    public boolean removeHoliday(int holidayId) throws SQLException {
        return holidayDAO.deleteHoliday(holidayId);
    }

    public void displayHolidayCalendar(int year) throws SQLException {
        List<Holiday> holidays = holidayDAO.getHolidaysByYear(year);
        System.out.println("\n=== Holiday Calendar " + year + " ===");
        if (holidays.isEmpty()) {
            System.out.println("No holidays configured for " + year);
        } else {
            for (Holiday holiday : holidays) {
                System.out.printf("%-20s : %s\n", holiday.getHolidayName(), holiday.getHolidayDate());
                if (holiday.getDescription() != null && !holiday.getDescription().isEmpty()) {
                    System.out.println("   Description: " + holiday.getDescription());
                }
            }
        }
    }
}