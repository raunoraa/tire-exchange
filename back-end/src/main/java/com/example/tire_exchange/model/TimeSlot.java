package com.example.tire_exchange.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record TimeSlot(String timeSlotId, LocalDate date, LocalTime time, String siteId, String siteName,
                       String siteAddress, List<String> vehicleTypes) implements Comparable<TimeSlot> {

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
