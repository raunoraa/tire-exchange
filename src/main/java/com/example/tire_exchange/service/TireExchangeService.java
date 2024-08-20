package com.example.tire_exchange.service;

import com.example.tire_exchange.config.ApiConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All back-end services
 */
@Service
public class TireExchangeService {

    private final RestTemplate restTemplate;
    private final ApiConfig apiConfig;

    public TireExchangeService(ApiConfig apiConfig) {
        this.restTemplate = new RestTemplate();
        this.apiConfig = apiConfig;
    }

    public Map<String, String> fetchAllTireData(){
        List<String> apiUrls = apiConfig.getApiUrls();
        Map<String, String> tireDataMap = new HashMap<>();

        for (String apiUrl : apiUrls) {
            try{
                // TODO: Find the right API postfix.
                String apiPostfix = "";
                String data = restTemplate.getForObject(apiUrl + apiPostfix, String.class);
            } catch (Exception e) {
                tireDataMap.put(apiUrl, "Failed to fetch data!");
            }
        }

        return tireDataMap;
    }

    // Example method to get data from another app
    public String getTireData() {
        String url = "https://other-tire-exchange-api.com/api/tires";
        return restTemplate.getForObject(url, String.class);
    }

    // Example method to post data to another app
    public String postTireData(Object requestData) {
        String url = "https://other-tire-exchange-api.com/api/tires";
        return restTemplate.postForObject(url, requestData, String.class);
    }
}