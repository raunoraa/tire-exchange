package com.example.tire_exchange.service;

import java.util.List;

public interface TireExchangeServiceTemplate {
    List<String> getAvailableTimes(String dateFrom, String dateTo);
    void bookTime(String bookId, String contactInformation);
}
