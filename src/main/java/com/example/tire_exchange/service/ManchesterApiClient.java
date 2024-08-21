package com.example.tire_exchange.service;

import java.util.List;

public class ManchesterApiClient implements TireExchangeServiceTemplate{
    @Override
    public List<String> getAvailableTimes(String dateFrom, String dateTo) {
        return List.of();
    }

    @Override
    public void bookTime(String bookId, String contactInformation) {

    }
}
