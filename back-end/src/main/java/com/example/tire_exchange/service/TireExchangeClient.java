package com.example.tire_exchange.service;

import com.example.tire_exchange.config.TireExchangeSitesProperties;
import com.example.tire_exchange.model.BookingResponse;
import com.example.tire_exchange.model.TimeSlot;

import java.util.List;

public interface TireExchangeClient {
    TireExchangeSitesProperties.ExchangeSite getExchangeSite();

    List<TimeSlot> getAvailableTimes(String dateFrom, String dateTo);

    BookingResponse bookTime(String bookId, String contactInformation);
}
