package com.example.tire_exchange.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
@ConfigurationProperties(prefix = "tire-exchange-sites")
public class TireExchangeSitesProperties {

    private List<ExchangeSite> ExchangeSites;

    public void setSites(List<ExchangeSite> ExchangeSites) {
        this.ExchangeSites = ExchangeSites;
    }

    @Getter
    public static class ExchangeSite {
        private String name;
        private String address;
        private List<String> vehicleTypes;
        private String apiBaseUrl;
    }
}
