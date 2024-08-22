package com.example.tire_exchange.service;

import com.example.tire_exchange.config.TireExchangeSitesProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

public class LondonApiClient implements TireExchangeClient{

    private final RestTemplate restTemplate;
    private final TireExchangeSitesProperties.ExchangeSite exchangeSite;

    public LondonApiClient(RestTemplate restTemplate, TireExchangeSitesProperties properties) {
        this.restTemplate = restTemplate;
        this.exchangeSite = properties.getExchangeSites().stream()
                .filter(s -> s.getSiteId().equals("2"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("London site not found!"));
    }

    @Override
    public TireExchangeSitesProperties.ExchangeSite getExchangeSite() {
        return exchangeSite;
    }

    /**
     *
     * @param xmlResponse
     * @return
     */
    private List<Map.Entry<String, Map.Entry<LocalDate, LocalTime>>> parseXmlAndExtractTimes(String xmlResponse){
        List<Map.Entry<String, Map.Entry<LocalDate, LocalTime>>> availableTimes = new ArrayList<>();
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

                Map.Entry<LocalDate, LocalTime> dateTimePair = new AbstractMap.SimpleEntry<>(date, time);
                availableTimes.add(new AbstractMap.SimpleEntry<>(uuid, dateTimePair));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML response", e);
        }
        return availableTimes;
    }

    /**
     *
     * @param sortableList
     */
    private void sortDateTimes(List<Map.Entry<String, Map.Entry<LocalDate, LocalTime>>> sortableList){
        // Sort the list first by time and then by date
        sortableList.sort(Comparator
                .comparing((Map.Entry<String, Map.Entry<LocalDate, LocalTime>> entry) -> entry.getValue().getValue()) // Sort by time
                .thenComparing(entry -> entry.getValue().getKey())); // Then by date
    }

    @Override
    public List<Map.Entry<String, Map.Entry<LocalDate, LocalTime>>> getAvailableTimes(String dateFrom, String dateTo) {

        String requestUrl = exchangeSite.getApiBaseUrl() + "tire-change-times/available?from=" + dateFrom + "&until=" + dateTo;

        // Fetch XML response from the API
        String response = restTemplate.getForObject(requestUrl, String.class);

        List<Map.Entry<String, Map.Entry<LocalDate, LocalTime>>> formattedResponse = parseXmlAndExtractTimes(response);
        sortDateTimes(formattedResponse);

        return formattedResponse;
    }

    @Override
    public void bookTime(String bookId, String contactInformation) {
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

        // Send the PUT request with RestTemplate
        restTemplate.put(url, requestEntity);
    }
}
