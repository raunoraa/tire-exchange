package com.example.tire_exchange.service;

import com.example.tire_exchange.config.TireExchangeSitesProperties;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface TireExchangeClient {
    TireExchangeSitesProperties.ExchangeSite getExchangeSite();
    List<Map.Entry<LocalDate, LocalTime>> getAvailableTimes(String dateFrom, String dateTo);
    void bookTime(String bookId, String contactInformation);
}
