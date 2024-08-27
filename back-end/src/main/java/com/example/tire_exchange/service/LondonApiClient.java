package com.example.tire_exchange.service;

import com.example.tire_exchange.config.TireExchangeSitesProperties;
import com.example.tire_exchange.model.BookingResponse;
import com.example.tire_exchange.model.TimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class LondonApiClient implements TireExchangeClient {

    private final RestTemplate restTemplate;
    private final TireExchangeSitesProperties.ExchangeSite exchangeSite;

    @Autowired
    public LondonApiClient(RestTemplate restTemplate, TireExchangeSitesProperties properties) {
        this.restTemplate = restTemplate;
        this.exchangeSite = properties.getExchangeSites().stream()
                .filter(s -> s.getSiteId().equals("1"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("London site not found!"));
    }

    @Override
    public TireExchangeSitesProperties.ExchangeSite getExchangeSite() {
        return exchangeSite;
    }

    /**
     * @param xmlResponse
     * @return
     */
    private List<TimeSlot> parseXmlAndExtractTimes(String xmlResponse) {

        List<TimeSlot> availableTimes = new ArrayList<>();

        try {
            // Set up document builder and parse XML string
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlResponse)));

            // Set up XPath for querying the XML
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            // Extract all <uuid> elements under <availableTime>
            NodeList uuidNodes = (NodeList) xpath.evaluate("//availableTime/uuid", document, XPathConstants.NODESET);

            // Extract all <time> elements under <availableTime>
            NodeList timeNodes = (NodeList) xpath.evaluate("//availableTime/time", document, XPathConstants.NODESET);

            // Parse and add each uuid and time to the list
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            for (int i = 0; i < uuidNodes.getLength(); i++) {
                String uuid = uuidNodes.item(i).getTextContent();
                String timeString = timeNodes.item(i).getTextContent();

                LocalDate date = LocalDate.parse(timeString, formatter);
                LocalTime time = LocalTime.parse(timeString, formatter);

                TimeSlot timeSlot = new TimeSlot(
                        uuid,
                        date,
                        time,
                        exchangeSite.getSiteId(),
                        exchangeSite.getName(),
                        exchangeSite.getAddress(),
                        exchangeSite.getVehicleTypes()
                );
                availableTimes.add(timeSlot);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML response", e);
        }
        return availableTimes;
    }

    @Override
    public List<TimeSlot> getAvailableTimes(String dateFrom, String dateTo) {

        String requestUrl = exchangeSite.getApiBaseUrl() + "tire-change-times/available?from=" + dateFrom + "&until=" + dateTo;

        // Fetch XML response from the API
        String response = restTemplate.getForObject(requestUrl, String.class);

        return parseXmlAndExtractTimes(response);
    }

    @Override
    public BookingResponse bookTime(String bookId, String contactInformation) {
        String url = exchangeSite.getApiBaseUrl() + "tire-change-times/" + bookId + "/booking";

        // Construct the XML body as a string
        String xmlBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<london.tireChangeBookingRequest>\n" +
                "    <contactInformation>" + contactInformation + "</contactInformation>\n" +
                "</london.tireChangeBookingRequest>";

        // Set up headers to specify that we're sending XML
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        // Wrap the XML body in an HttpEntity with the headers
        HttpEntity<String> requestEntity = new HttpEntity<>(xmlBody, headers);

        try {
            // Perform the HTTP request
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
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
