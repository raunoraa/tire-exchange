package com.example.tire_exchange.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TireExchangeService {

    private final List<TireExchangeClient> tireExchangeClients;

    @Autowired
    public TireExchangeService(List<TireExchangeClient> tireExchangeClients) {
        this.tireExchangeClients = tireExchangeClients;
    }

    /**
     * Helper method for merging the sorted lists in-place from client methods.
     *
     * @param list1 The list, to which we are adding elements into.
     * @param list2 The list we are taking elements from.
     */
    private void mergeSortedLists(List<Map.Entry<String, Map.Entry<LocalDate, LocalTime>>> list1, List<Map.Entry<String, Map.Entry<LocalDate, LocalTime>>> list2) {

        //If there are no elements in the first list yet, just add all the elements from the second list and return.
        if (list1.isEmpty()){
            list1.addAll(list2);
            return;
        }

        int i = list1.size() - 1;
        int j = list2.size() - 1;
        int k = list1.size() + list2.size() - 1;

        // Expand list1 to accommodate all elements
        list1.addAll(list2);

        // Merge lists starting from the end to avoid overwriting elements
        while (i >= 0 && j >= 0) {

            LocalTime time1 = list1.get(i).getValue().getValue(); // Assuming getTime() returns LocalDateTime
            LocalTime time2 = list2.get(j).getValue().getValue();

            if (time1.isAfter(time2)) {
                list1.set(k--, list1.get(i--));
            } else if (time1.isBefore(time2)) {
                list1.set(k--, list2.get(j--));
            } else {
                // If times are equal, compare by date
                if (list1.get(i).getValue().getKey().isAfter(list2.get(j).getValue().getKey())) {
                    list1.set(k--, list1.get(i--));
                } else {
                    list1.set(k--, list2.get(j--));
                }
            }
        }

        // If there are remaining elements in list2, copy them
        while (j >= 0) {
            list1.set(k--, list2.get(j--));
        }

        // Remaining elements in list1 are already in place
    }

    public List<Map.Entry<String, Map.Entry<LocalDate, LocalTime>>> getAvailableTimesFromRange(String from, String to) {
        List<Map.Entry<String, Map.Entry<LocalDate, LocalTime>>> allAvailableTimes = new ArrayList<>();

        // Iterate over all clients and collect their available times
        for (TireExchangeClient client : tireExchangeClients) {
            mergeSortedLists(allAvailableTimes, client.getAvailableTimes(from, to));
        }

        return allAvailableTimes;
    }

    /**
     * Helper method for getting the correct client based on the site name
     * (we assume everywhere that site names are unique).
     *
     * @param siteName Provided site name as a string.
     * @return Correct tire exchange client object based on the name.
     */
    private TireExchangeClient getCorrectClient(String siteName) {
        // Find the right client by name
        TireExchangeClient tireExchangeClient = null;

        for (TireExchangeClient client : tireExchangeClients) {
            if (client.getExchangeSite().getName().equals(siteName)) {
                tireExchangeClient = client;
                break;
            }
        }

        if (tireExchangeClient == null) {
            throw new RuntimeException("Tire exchange site with the name '" + siteName + "' was not found!");
        }

        return tireExchangeClient;
    }

    // Gets available times only from certain exchange site(s).
    public List<Map.Entry<String, Map.Entry<LocalDate, LocalTime>>> getAvailableTimesFromRange(String from, String to, List<String> siteNames) {

        List<TireExchangeClient> observableTireExchangeClients = new ArrayList<>();

        // Find the right clients by name
        for (String siteName : siteNames) {
            observableTireExchangeClients.add(getCorrectClient(siteName));
        }

        List<Map.Entry<String, Map.Entry<LocalDate, LocalTime>>> allAvailableTimes = new ArrayList<>();

        // Iterate over all clients and collect their available times
        for (TireExchangeClient client : observableTireExchangeClients) {
            mergeSortedLists(allAvailableTimes, client.getAvailableTimes(from, to));
        }

        return allAvailableTimes;
    }

}