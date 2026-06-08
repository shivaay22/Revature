package com.revworkforce.service;

import com.revworkforce.dao.HolidayDAO;
import com.revworkforce.model.Holiday;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

    @Mock
    private HolidayDAO holidayDAO;

    @InjectMocks
    private HolidayService holidayService;

    private Holiday holiday;

    @BeforeEach
    void setup() {
        holiday = new Holiday();
        holiday.setHolidayName("Diwali");
        holiday.setDescription("Festival of lights");
        holiday.setHolidayDate(new Date(System.currentTimeMillis() + 86400000));
    }


    @Test
    void testGetHolidaysForYear() throws SQLException {
        when(holidayDAO.getHolidaysByYear(2026))
                .thenReturn(List.of(holiday));

        List<Holiday> result = holidayService.getHolidaysForYear(2026);

        assertEquals(1, result.size());
    }

    @Test
    void testGetCurrentYearHolidays() throws SQLException {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        when(holidayDAO.getHolidaysByYear(currentYear))
                .thenReturn(List.of(holiday));

        List<Holiday> result = holidayService.getCurrentYearHolidays();

        assertEquals(1, result.size());
    }



    @Test
    void testAddHolidaySuccess() throws SQLException {
        when(holidayDAO.addHoliday(holiday)).thenReturn(true);

        assertTrue(holidayService.addHoliday(holiday));
    }

    @Test
    void testAddHolidayFail() throws SQLException {
        when(holidayDAO.addHoliday(holiday)).thenReturn(false);

        assertFalse(holidayService.addHoliday(holiday));
    }

    @Test
    void testRemoveHolidaySuccess() throws SQLException {
        when(holidayDAO.deleteHoliday(1)).thenReturn(true);

        assertTrue(holidayService.removeHoliday(1));
    }

    @Test
    void testRemoveHolidayFail() throws SQLException {
        when(holidayDAO.deleteHoliday(1)).thenReturn(false);

        assertFalse(holidayService.removeHoliday(1));
    }


    @Test
    void testDisplayHolidayCalendarWithData() throws SQLException {
        when(holidayDAO.getHolidaysByYear(2026))
                .thenReturn(List.of(holiday));

        // Capture console output
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        holidayService.displayHolidayCalendar(2026);

        String output = out.toString();

        assertTrue(output.contains("Holiday Calendar 2026"));
        assertTrue(output.contains("Diwali"));
        assertTrue(output.contains("Festival of lights"));
    }

    @Test
    void testDisplayHolidayCalendarEmpty() throws SQLException {
        when(holidayDAO.getHolidaysByYear(2026))
                .thenReturn(Collections.emptyList());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        holidayService.displayHolidayCalendar(2026);

        String output = out.toString();

        assertTrue(output.contains("No holidays configured"));
    }
}