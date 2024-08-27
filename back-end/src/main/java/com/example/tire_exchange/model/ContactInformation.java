package com.example.tire_exchange.model;

public record ContactInformation(String fullName, String phoneNumber, String email) {

    @Override
    public String toString() {
        return "{" +
                "Full Name='" + fullName + '\'' +
                "; Phone Number=" + phoneNumber +
                "; E-mail='" + email + '\'' +
                '}';
    }
}
