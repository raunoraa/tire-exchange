package com.example.tire_exchange.service;


import com.example.tire_exchange.model.TimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TireExchangeService {

    public final List<TireExchangeClient> tireExchangeClients;

    @Autowired
    public TireExchangeService(List<TireExchangeClient> tireExchangeClients) {
        this.tireExchangeClients = tireExchangeClients;
    }

    /**
     * Helper method for merging the sorted lists in-place from client methods.
     * I think we can assume that available times get request from api returns dates in ascending order.
     *
     * @param expandableList The list, to which we are adding elements into.
     * @param secondList The list we are taking elements from.
     */
    private void mergeSortedLists(List<TimeSlot> expandableList, List<TimeSlot> secondList) {

        //If there are no elements in the first list yet, just add all the elements from the second list and return.
        if (expandableList.isEmpty()){
            expandableList.addAll(secondList);
            return;
        }

        int i = expandableList.size() - 1;
        int j = secondList.size() - 1;
        int k = expandableList.size() + secondList.size() - 1;

        // Expand list1 to accommodate all elements (basically placeholders at this state).
        expandableList.addAll(secondList);

        // Merge lists starting from the end to avoid overwriting elements
        while (i >= 0 && j >= 0) {

            if (expandableList.get(i).compareTo(secondList.get(j)) > 0) {
                // When timeslot in the first list happens to be after the second list timeslot,
                // add it to the current list position.
                expandableList.set(k--, expandableList.get(i--));
            } else {
                expandableList.set(k--, secondList.get(j--));
            }
        }

        // If there are remaining elements in secondList, copy them
        while (j >= 0) {
            expandableList.set(k--, secondList.get(j--));
        }

        // Remaining elements in expandableList are already in place.
    }

    /**
     * Helper method for getting the correct client based on the site ID.
     *
     * @param siteId Provided site ID as a string.
     * @return Correct tire exchange client object based on the name.
     */
    private TireExchangeClient getCorrectClient(String siteId) {
        // Find the right client by name
        TireExchangeClient tireExchangeClient = null;

        for (TireExchangeClient client : tireExchangeClients) {
            if (client.getExchangeSite().getSiteId().equals(siteId)) {
                tireExchangeClient = client;
                break;
            }
        }

        if (tireExchangeClient == null) {
            throw new RuntimeException("Tire exchange site with the ID '" + siteId + "' was not found!");
        }

        return tireExchangeClient;
    }

    private boolean matchesVehicleTypes(TireExchangeClient client, List<String> vehicleTypes, String vehicleTypeMatchMode) {
        Set<String> clientVehicleTypes = new HashSet<>(client.getExchangeSite().getVehicleTypes());

        if ("any".equals(vehicleTypeMatchMode)) {
            return !Collections.disjoint(clientVehicleTypes, vehicleTypes);
        } else {
            return clientVehicleTypes.containsAll(vehicleTypes);
        }
    }

    // Gets available times only from certain exchange site(s).
    public List<TimeSlot> getAvailableTimesFromRange(String from, String to, List<String> siteIDs, List<String> vehicleTypes, String vehicleTypeMatchMode) {

        // If no siteIDs are provided, fetch from all the sites by default.
        List<String> effectiveSiteIDs = (siteIDs == null || siteIDs.isEmpty()) ?
                tireExchangeClients.stream()
                        .map(client -> client.getExchangeSite().getSiteId())
                        .toList()
                : siteIDs;

        List<TireExchangeClient> observableTireExchangeClients = tireExchangeClients.stream()
                .filter(client -> effectiveSiteIDs.contains(client.getExchangeSite().getSiteId()))
                .filter(client -> vehicleTypes == null || vehicleTypes.isEmpty() || matchesVehicleTypes(client, vehicleTypes, vehicleTypeMatchMode))
                .toList();

        List<TimeSlot> allAvailableTimes = new ArrayList<>();

        // Iterate over all clients and collect their available times
        for (TireExchangeClient client : observableTireExchangeClients) {
            mergeSortedLists(allAvailableTimes, client.getAvailableTimes(from, to));
        }

        // Since we can assume that APIs return data in a sorted manner already,
        // then merging (time complexity of it is roughly: number_of_apis * (first_list_size  + second_list_size))
        // has less time complexity than collections.sort (time complexity roughly: O(n*Log(n)), where n is
        // the sum of available times provided by all the APIs).

        return allAvailableTimes;
    }

    public void bookTime(String siteId, String bookId, String contactInformation){
        TireExchangeClient client = getCorrectClient(siteId);
        client.bookTime(bookId, contactInformation);
    }

}