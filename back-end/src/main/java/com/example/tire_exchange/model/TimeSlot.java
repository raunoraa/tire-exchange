package com.example.tire_exchange.model;

import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
public class TimeSlot implements Comparable<TimeSlot>{

    private final String timeSlotId;
    private final LocalDate date;
    private final LocalTime time;

    private final String siteId;
    private final String siteName;
    private final String siteAddress;
    private final List<String> vehicleTypes;

    public TimeSlot(String timeSlotId, LocalDate date, LocalTime time, String siteId, String siteName, String siteAddress, List<String> vehicleTypes) {
        this.timeSlotId = timeSlotId;
        this.date = date;
        this.time = time;
        this.siteId = siteId;
        this.siteName = siteName;
        this.siteAddress = siteAddress;
        this.vehicleTypes = vehicleTypes;
    }

    // Necessary if we want to sort the timeslots in ascending order by date and time.
    @Override
    public int compareTo(TimeSlot other) {

        // Compare by LocalDate first (earlier dates first)
        int dateComparison = this.date.compareTo(other.date);
        if (dateComparison != 0) {
            return dateComparison;
        }
        // If dates are equal, compare by LocalTime (earlier times first)
        return this.time.compareTo(other.time);
    }
}
