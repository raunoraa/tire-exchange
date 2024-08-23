package com.example.tire_exchange.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "tire-exchange-sites")
public class TireExchangeSitesProperties {

    private List<ExchangeSite> exchangeSites;

    @Getter
    @Setter
    public static class ExchangeSite {
        private String siteId;
        private String name;
        private String address;
        private List<String> vehicleTypes;
        private String apiBaseUrl;
    }
}
