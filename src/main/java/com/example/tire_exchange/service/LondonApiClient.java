package com.example.tire_exchange.service;

import com.example.tire_exchange.config.TireExchangeSitesProperties;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class LondonApiClient implements TireExchangeClient{

    private final RestTemplate restTemplate;
    private final TireExchangeSitesProperties.ExchangeSite exchangeSite;

    public LondonApiClient(RestTemplate restTemplate, TireExchangeSitesProperties properties) {
        this.restTemplate = restTemplate;
        this.exchangeSite = properties.getExchangeSites().stream()
                .filter(s -> s.getName().equals("Manchester"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Manchester site not found!"));
    }

    @Override
    public TireExchangeSitesProperties.ExchangeSite getExchangeSite() {
        return exchangeSite;
    }

    @Override
    public List<Map.Entry<LocalDate, LocalTime>> getAvailableTimes(String dateFrom, String dateTo) {
        return List.of();
    }

    @Override
    public void bookTime(String bookId, String contactInformation) {

    }
}
