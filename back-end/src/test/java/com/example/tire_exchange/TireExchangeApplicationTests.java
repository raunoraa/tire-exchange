package com.example.tire_exchange;

import com.example.tire_exchange.config.TireExchangeSitesProperties;
import com.example.tire_exchange.model.BookingResponse;
import com.example.tire_exchange.model.ContactInformation;
import com.example.tire_exchange.model.TimeSlot;
import com.example.tire_exchange.service.TireExchangeClient;
import com.example.tire_exchange.service.TireExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TireExchangeApplicationTests {

	private TireExchangeService tireExchangeService;

	@Mock
	private TireExchangeClient client1;

	@Mock
	private TireExchangeClient client2;

	@Mock
	private TireExchangeSitesProperties.ExchangeSite site1;

	@Mock
	private TireExchangeSitesProperties.ExchangeSite site2;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		List<TireExchangeClient> clients = List.of(client1, client2);
		tireExchangeService = new TireExchangeService(clients);

		// Set up the mock ExchangeSite objects
		Mockito.when(site1.getSiteId()).thenReturn("site1");
		Mockito.when(site2.getSiteId()).thenReturn("site2");

		// Set up the mock TireExchangeClient objects to return the mock ExchangeSite objects
		Mockito.when(client1.getExchangeSite()).thenReturn(site1);
		Mockito.when(client2.getExchangeSite()).thenReturn(site2);
	}

	@Test
	void contextLoads() {
	}

	@Test
	void testGetAvailableTimesFromRange() {
		// Setup mock data
		List<TimeSlot> client1Times = List.of(new TimeSlot("1", LocalDate.parse("2024-08-20"), LocalTime.parse("10:00:00"), "site1", "Site 1", "SomeStreet 24, SomeCity", List.of("type1","type2")));
		List<TimeSlot> client2Times = List.of(new TimeSlot("a", LocalDate.parse("2024-08-22"), LocalTime.parse("12:00:00"), "site2", "Site 2", "SomeStreet 24, SomeCity", List.of("type1","type2")));

		Mockito.when(client1.getExchangeSite().getSiteId()).thenReturn("site1");
		Mockito.when(client1.getAvailableTimes("2024-08-10", "2024-08-30")).thenReturn(client1Times);

		Mockito.when(client2.getExchangeSite().getSiteId()).thenReturn("site2");
		Mockito.when(client2.getAvailableTimes("2024-08-10", "2024-08-30")).thenReturn(client2Times);

		// Execute the method to be tested
		List<TimeSlot> result = tireExchangeService.getAvailableTimesFromRange("2024-08-10", "2024-08-30", null, null, "any");

		List<TimeSlot> client1expected = List.of(new TimeSlot("1", LocalDate.parse("2024-08-20"), LocalTime.parse("10:00:00"), "site1", "Site 1", "SomeStreet 24, SomeCity", List.of("type1","type2")),
				new TimeSlot("a", LocalDate.parse("2024-08-22"), LocalTime.parse("12:00:00"), "site2", "Site 2", "SomeStreet 24, SomeCity", List.of("type1","type2")));


		assertEquals(2, result.size());
		assertEquals(client1expected, result);
	}

	@Test
	void testBookTime() {
		// Setup mock client
		Mockito.when(client1.getExchangeSite().getSiteId()).thenReturn("site1");

		// Mock the booking response
		BookingResponse mockResponse = new BookingResponse(200);
		Mockito.when(client1.bookTime(Mockito.anyString(), Mockito.anyString())).thenReturn(mockResponse);

		// Execute the method to be tested
		BookingResponse response = tireExchangeService.bookTime("site1", "bookingId123", new ContactInformation("John Doe", "12345", "john.doe@example.com"));

		// Verify the booking response
		assertEquals(200, response.getStatusCode());
		assertEquals("Time Booked Successfully!", response.getStatusMessage());

		// Verify that the correct client's bookTime method was called
		Mockito.verify(client1).bookTime(Mockito.eq("bookingId123"), Mockito.anyString());
	}

}
