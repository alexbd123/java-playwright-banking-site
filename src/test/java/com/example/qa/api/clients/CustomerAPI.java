package com.example.qa.api.clients;

import com.example.qa.api.dtos.CustomerDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;


public class CustomerAPI {

    private final APIRequestContext request;

    public CustomerAPI(APIRequestContext request) {
        this.request = request;
    }

    public Integer sendGetRequestToRetrieveCustomerId(String username, String password) throws JsonProcessingException {
        APIResponse response = request.get(String.format("login/%s/%s", username, password));
        ObjectMapper mapper = new ObjectMapper();
        CustomerDto customer = mapper.readValue(response.text(), CustomerDto.class);
        return customer.getId();
    }
}
