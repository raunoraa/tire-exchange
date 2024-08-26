package com.example.tire_exchange.service;

import com.example.tire_exchange.config.TireExchangeSitesProperties;
import com.example.tire_exchange.model.BookingResponse;
import com.example.tire_exchange.model.TimeSlot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ManchesterApiClient implements TireExchangeClient{

    private final RestTemplate restTemplate;
    private final TireExchangeSitesProperties.ExchangeSite exchangeSite;
    private final ObjectMapper objectMapper;

    @Autowired
    public ManchesterApiClient(RestTemplate restTemplate, TireExchangeSitesProperties properties, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.exchangeSite = properties.getExchangeSites().stream()
                .filter(s -> s.getSiteId().equals("2"))
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
    public List<TimeSlot> getAvailableTimes(String dateFrom, String dateTo) {

        LocalDate from = convertStringToLocalDate(dateFrom);
        LocalDate to = convertStringToLocalDate(dateTo);

        String baseUrl = exchangeSite.getApiBaseUrl();
        String requestUrl = baseUrl + "tire-change-times?from=" + from;

        String response = restTemplate.getForObject(requestUrl, String.class);
        List<Map<String, Object>> responseList = parseJsonResponse(response);

        // Extract date and time strings from the JSON response and put them in the list of dateTimeStrings.
        List<TimeSlot> dateTimes = new ArrayList<>();

        for (Map<String, Object> entry : responseList) {

            // Only add available timeslots to the list.
            Boolean available = (Boolean) entry.get("available");

            if (Boolean.TRUE.equals(available)) {
                String timeString = (String) entry.get("time");
                LocalDateTime localDateTime = convertToLocalDateTime(timeString);
                // Only add such available timeslots to the list, which are before the upper limit of the dates.
                if (localDateTime.toLocalDate().isBefore(to)) {
                    TimeSlot timeSlot = new TimeSlot(
                            entry.get("id").toString(),
                            localDateTime.toLocalDate(),
                            localDateTime.toLocalTime(),
                            exchangeSite.getSiteId(),
                            exchangeSite.getName(),
                            exchangeSite.getAddress(),
                            exchangeSite.getVehicleTypes()
                    );
                    dateTimes.add(timeSlot);
                }
            }
        }
        return dateTimes;
    }

    @Override
    public BookingResponse bookTime(String bookId, String contactInformation) {

        String url = exchangeSite.getApiBaseUrl() + "tire-change-times/" + bookId + "/booking";

        //System.out.println(url);

        // Set up headers to specify that we're sending and accepting JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("contactInformation", contactInformation);

        // Wrap the JSON body in an HttpEntity with the headers
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        //System.out.println(requestEntity);


        try {
            // Perform the HTTP request
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            // Extract the status code
            int statusCode = response.getStatusCode().value();
            return new BookingResponse(statusCode);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Handle client and server errors.
            int statusCode = e.getStatusCode().value();
            return new BookingResponse(statusCode);
        } catch (RestClientException e) {
            // Handle other RestClientExceptions
            return new BookingResponse(500);
        }

    }
}
