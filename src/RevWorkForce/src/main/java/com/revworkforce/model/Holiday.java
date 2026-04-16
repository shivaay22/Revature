package com.revworkforce.model;

import java.util.Date;

public class Holiday {
    private int holidayId;
    private String holidayName;
    private Date holidayDate;
    private int year;
    private String description;

    public Holiday() {}

    public Holiday(String holidayName, Date holidayDate, int year) {
        this.holidayName = holidayName;
        this.holidayDate = holidayDate;
        this.year = year;
    }

    public int getHolidayId() {
        return holidayId;
    }

    public void setHolidayId(int holidayId) {
        this.holidayId = holidayId;
    }

    public String getHolidayName() {
        return holidayName;
    }

    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }

    public Date getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(Date holidayDate) {
        this.holidayDate = holidayDate;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}