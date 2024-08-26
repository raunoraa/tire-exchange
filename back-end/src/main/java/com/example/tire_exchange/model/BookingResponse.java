package com.example.tire_exchange.model;

import lombok.Getter;

@Getter
public class BookingResponse {

    private final int statusCode;
    private final String statusMessage;

    public BookingResponse(int statusCode) {

        this.statusCode = statusCode;
        switch (statusCode) {
            case 200:
                this.statusMessage = "Time Booked Successfully!";
                break;
            case 400:
                this.statusMessage = "Error: Bad Request";
                break;
            case 422:
                this.statusMessage = "The tire change time has already been booked by another contact!";
                break;
            case 500:
                this.statusMessage = "Error: Internal Server Error";
                break;
            default:
                this.statusMessage = "Error: Unknown Error";
                break;
        }

    }

}
