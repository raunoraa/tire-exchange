package com.example.tire_exchange.service;

import com.example.tire_exchange.config.TireExchangeSitesProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cglib.core.Local;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ManchesterApiClient implements TireExchangeClient{

    private final RestTemplate restTemplate;
    private final TireExchangeSitesProperties.ExchangeSite exchangeSite;
    private final ObjectMapper objectMapper;

    public ManchesterApiClient(RestTemplate restTemplate, TireExchangeSitesProperties properties, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.exchangeSite = properties.getExchangeSites().stream()
                .filter(s -> s.getName().equals("Manchester"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Manchester site not found!"));
        this.objectMapper = objectMapper;
    }

    @Override
    public TireExchangeSitesProperties.ExchangeSite getExchangeSite() {
        return exchangeSite;
    }

    /**
     * Small helper method to convert the input datetime string to LocalDateTime object.
     *
     * @param dateTimeString Input datetime string.
     * @return LocalDatetime object based on the input datetime string.
     */
    private LocalDateTime convertToLocalDateTime(String dateTimeString){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        ZonedDateTime date = ZonedDateTime.parse(dateTimeString, formatter);
        return date.toLocalDateTime();
    }

    /**
     * Helper method for converting the dates and times to suitable format
     * and sorting them in ascending order.
     *
     * @param dateTimes Dates and times as a list of LocalDateTime objects.
     * @return A list of sorted (ascending by date and time) pairs, where
     * first el. in pair is date and second el. is time.
     */
    private List<Map.Entry<LocalDate, LocalTime>> convertAndSort(List<LocalDateTime> dateTimes){

        List<Map.Entry<LocalDate, LocalTime>> dateTimePairs = new ArrayList<>();

        for (LocalDateTime dateTime: dateTimes){
            LocalDate localDate = dateTime.toLocalDate();
            LocalTime localTime = dateTime.toLocalTime();
            dateTimePairs.add(new AbstractMap.SimpleEntry<>(localDate, localTime));
        }

        // Sort the list first by time and then by date
        dateTimePairs.sort(Comparator
                .comparing(Map.Entry<LocalDate, LocalTime>::getValue) // Sort by time
                .thenComparing(Map.Entry::getKey)); // Then by date

        return dateTimePairs;
    }

    /**
     * Small helper method for converting the date string to LocalDate object.
     *
     * @param dateString Date as string.
     * @return LocalDate object based on the date given as string.
     */
    private LocalDate convertStringToLocalDate(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        return LocalDate.parse(dateString, formatter);
    }

    private List<Map<String, Object>> parseJsonResponse(String jsonResponse){
        try {
            return objectMapper.readValue(jsonResponse, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON response!", e);
        }
    }

    @Override
    public List<Map.Entry<LocalDate, LocalTime>> getAvailableTimes(String dateFrom, String dateTo) {

        LocalDate from = convertStringToLocalDate(dateFrom);
        LocalDate to = convertStringToLocalDate(dateTo);

        String baseUrl = exchangeSite.getApiBaseUrl();
        String requestUrl = baseUrl + "tire-change-times?from=" + from.toString();

        String response = restTemplate.getForObject(baseUrl + requestUrl, String.class);
        List<Map<String, Object>> responseList = parseJsonResponse(response);

        // Extract date and time strings from the JSON response and put them in the list of dateTimeStrings.
        List<LocalDateTime> dateTimes = new ArrayList<>();
        for (Map<String, Object> entry : responseList) {

            // Only add available times to the list.
            Boolean available = (Boolean) entry.get("available");
            if (Boolean.TRUE.equals(available)) {
                String timeString = (String) entry.get("time");
                LocalDateTime localDateTime = convertToLocalDateTime(timeString);

                // Only add such available times to the list, which are before the upper limit of the dates.
                if (localDateTime.toLocalDate().isAfter(to)) {
                    dateTimes.add(localDateTime);
                }
            }
        }

        return convertAndSort(dateTimes);
    }

    @Override
    public void bookTime(String bookId, String contactInformation) {

    }
}
