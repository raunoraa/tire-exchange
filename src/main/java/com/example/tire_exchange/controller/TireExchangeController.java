package com.example.tire_exchange.controller;

import com.example.tire_exchange.service.TireExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class' methods will be used in front-end to fetch the back-end data.
 */
@RestController
public class TireExchangeController {

    private final TireExchangeService tireExchangeService;

    @Autowired
    public TireExchangeController(TireExchangeService tireExchangeService) {
        this.tireExchangeService = tireExchangeService;
    }

    @GetMapping("/fetch-tires")
    public String fetchTires() {
        return tireExchangeService.getTireData();
    }

    @PostMapping("/submit-tire-data")
    public String submitTireData(@RequestBody Object requestData) {
        return tireExchangeService.postTireData(requestData);
    }
}