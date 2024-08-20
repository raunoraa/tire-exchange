package com.example.tire_exchange.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class ApiConfig {

    @Value("${tire_exchange.api.urls}")
    private String apiUrls;

    public List<String> getApiUrls() {
        return Arrays.asList(apiUrls.split(","));
    }
}