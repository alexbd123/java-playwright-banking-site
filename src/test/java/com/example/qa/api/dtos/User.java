package com.example.qa.api.dtos;

public record User(String firstName, String lastName, String address, String city, String state, String zipCode,
                   String phoneNumber, String ssn, String username, String password) {
}
