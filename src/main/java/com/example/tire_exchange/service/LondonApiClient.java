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
                .filter(s -> s.getName().equals("London"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("London site not found!"));
    }

    @Override
    public TireExchangeSitesProperties.ExchangeSite getExchangeSite() {
        return exchangeSite;
    }

    @Override
    public List<Map.Entry<String, Map.Entry<LocalDate, LocalTime>>> getAvailableTimes(String dateFrom, String dateTo) {

        String requestUrl = exchangeSite.getApiBaseUrl() + "/tire-change-times/available?from=" + dateFrom + "&until=" + dateTo;

        return null;
    }

    @Override
    public void bookTime(String bookId, String contactInformation) {

    }
}
