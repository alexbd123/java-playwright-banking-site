package com.example.qa.api;

import com.example.qa.api.dtos.AccountDto;
import com.example.qa.api.dtos.CustomerDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;

public class ApiHelper {

    ObjectMapper mapper;

    //Customer API helpers
    public Integer getCustomerIdFromLoginRequestResponse(APIResponse loginResponse) throws JsonProcessingException {
        mapper = new ObjectMapper();
        CustomerDto customer = mapper.readValue(loginResponse.text(), CustomerDto.class);
        return customer.getId();
    }

    //Account actions API helpers
    public Integer getAccountIdFromResponse(APIResponse response) throws JsonProcessingException {
        mapper = new ObjectMapper();
        AccountDto account = mapper.readValue(response.text(), AccountDto.class);
        return account.getId();
    }

    public Integer getOriginalAccountIdFromResponse(APIResponse response) throws JsonProcessingException {
        mapper = new ObjectMapper();
        String json = response.text();
        AccountDto[] accounts = mapper.readValue(json, AccountDto[].class);
        return accounts[0].getId();
    }
}
