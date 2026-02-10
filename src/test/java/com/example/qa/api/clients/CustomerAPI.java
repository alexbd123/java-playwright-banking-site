package com.example.qa.api.clients;

import com.example.qa.api.dtos.CustomerDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

public class CustomerAPI {

    private final APIRequestContext request;
    ObjectMapper mapper = new ObjectMapper();

    public CustomerAPI(APIRequestContext request) {
        this.request = request;
    }

    public CustomerDto sendGetRequestToLogIn(String username,
                                             String password) {
        APIResponse response = request.get(String.format("login/%s/%s",
                username,
                password));
        if (!response.ok()) {
            throw new IllegalStateException("Failed to get customer details through Log In request");
        }
        try {
            return mapper.readValue(response.text(), CustomerDto.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse customer details from Log In request");
        }
    }

}
