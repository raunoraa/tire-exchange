package com.example.tire_exchange.service;

import com.example.tire_exchange.config.TireExchangeSitesProperties;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class LondonApiClient implements TireExchangeServiceTemplate{

    private final RestTemplate restTemplate;
    private final TireExchangeSitesProperties properties;

    public LondonApiClient(RestTemplate restTemplate, TireExchangeSitesProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public List<String> getAvailableTimes(String dateFrom, String dateTo) {
        return List.of();
    }

    @Override
    public void bookTime(String bookId, String contactInformation) {

    }
}
