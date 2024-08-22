package com.example.tire_exchange.controller;

import com.example.tire_exchange.service.TireExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * This class' methods will be used in front-end to fetch the back-end data.
 */
@RestController
public class TireExchangeController {
    @Autowired
    private TireExchangeService tireExchangeService;

    // Date formatter to convert string dates
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    // 1. Get available times from all sites within the specified date range
    @GetMapping("/available-times")
    public List<Map.Entry<String, Map.Entry<LocalDate, LocalTime>>> getAvailableTimes(
            @RequestParam(name = "from", required = false) String fromDate,
            @RequestParam(name = "to", required = false) String toDate,
            @RequestParam(name = "sites", required = false) List<String> siteIDs) {

        // Set default date range if not provided
        LocalDate from = fromDate != null ? LocalDate.parse(fromDate, dateFormatter) : LocalDate.now();
        LocalDate to = toDate != null ? LocalDate.parse(toDate, dateFormatter) : from.plusDays(30);

        // If sites are not provided, then fetch from all sites by default
        if (siteIDs == null || siteIDs.isEmpty())
            return tireExchangeService.getAvailableTimesFromRange(from.toString(), to.toString());

        // Call the service method to fetch available times
        return tireExchangeService.getAvailableTimesFromRange(from.toString(), to.toString(), siteIDs);
    }

    // 2. Book a time slot at a specific site
    @PostMapping("/book-time")
    public void bookTime(
            @RequestParam("siteId") String siteId,
            @RequestParam("bookId") String bookId,
            @RequestParam("contactInformation") String contactInformation) {

        // Call the service method to book the time slot
        tireExchangeService.bookTime(siteId, bookId, contactInformation);
    }
}