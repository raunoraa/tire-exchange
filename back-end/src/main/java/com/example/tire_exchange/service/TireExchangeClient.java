package com.example.tire_exchange.service;

import com.example.tire_exchange.config.TireExchangeSitesProperties;
import com.example.tire_exchange.model.BookingResponse;
import com.example.tire_exchange.model.TimeSlot;

import java.util.List;

public interface TireExchangeClient {
    /**
     * All clients must be able to return their ExchangeSite property.
     *
     * @return TireExchangeSite ExchangeSite property.
     */
    TireExchangeSitesProperties.ExchangeSite getExchangeSite();

    /**
     * All clients must be able to return available times based on the provided date range.
     *
     * @param dateFrom From date.
     * @param dateTo To date.
     * @return List of available time objects, which carry all the necessary data.
     */
    List<TimeSlot> getAvailableTimes(String dateFrom, String dateTo);

    /**
     * All clients must be able to book a time and return a response
     * as a BookingResponse object, which carries all the necessary data.
     *
     * @param bookId BookingId as String (each client may have different ID system).
     * @param contactInformation ContactInformation as String (ContactInformation object conv.-d to String).
     * @return BookingResponse object based on the response received.
     */
    BookingResponse bookTime(String bookId, String contactInformation);
}
