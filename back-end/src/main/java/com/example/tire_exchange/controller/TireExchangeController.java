package com.example.tire_exchange.controller;

import com.example.tire_exchange.config.TireExchangeSitesProperties;
import com.example.tire_exchange.model.BookingResponse;
import com.example.tire_exchange.model.TimeSlot;
import com.example.tire_exchange.service.TireExchangeClient;
import com.example.tire_exchange.service.TireExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class' methods will be used in front-end to fetch the back-end data.
 */
@RestController
public class TireExchangeController {

    @Autowired
    private TireExchangeService tireExchangeService;

    // Date formatter to convert string dates
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    // Special get method for getting the data of defined tire exchange sites.
    @GetMapping("/get-config")
    public List<TireExchangeSitesProperties.ExchangeSite> exchangeSites(){
        List<TireExchangeSitesProperties.ExchangeSite> exchangeSitesList = new ArrayList<>();

        for (TireExchangeClient tireExchangeClient : tireExchangeService.tireExchangeClients){
            exchangeSitesList.add(tireExchangeClient.getExchangeSite());
        }

        return exchangeSitesList;
    }

    // Get available times from all sites within the specified date range
    @GetMapping("/available-times")
    public List<TimeSlot> getAvailableTimes(
            @RequestParam(name = "from", required = false) String fromDate,
            @RequestParam(name = "to", required = false) String toDate,
            @RequestParam(name = "sites", required = false) List<String> siteIDs,
            @RequestParam(name = "vehicleTypes", required = false) List<String> vehicleTypes,
            @RequestParam(name = "vehicleTypeMatchMode", required = false, defaultValue = "any") String VehicleTypeMatchMode
            ) {

        LocalDate from;
        LocalDate to;
        // Set default date range 30 days from current date (used when from or to dates are not provided,
        // this however should normally not happen).
        if (fromDate == null || toDate == null) {
            from = LocalDate.now();
            to = from.plusDays(30);
        } else {
            from = LocalDate.parse(fromDate, dateFormatter);
            to = LocalDate.parse(toDate, dateFormatter);
        }

        // Call the service method to fetch available times
        return tireExchangeService.getAvailableTimesFromRange(from.toString(), to.toString(), siteIDs, vehicleTypes, VehicleTypeMatchMode);
    }

    // Book a time slot at a specific site
    @PutMapping("/book-time")
    public BookingResponse bookTime(
            @RequestParam(name = "siteId") String siteId,
            @RequestParam(name = "bookId") String bookId,
            @RequestParam(name = "contactInformation") String contactInformation) {

        // Call the service method to book the time slot
        return tireExchangeService.bookTime(siteId, bookId, contactInformation);
    }
}