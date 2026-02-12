package com.example.qa.api.clients;

import com.example.qa.api.dtos.CustomerDto;
import com.example.qa.api.dtos.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

public class CustomerAPI {

    private final APIRequestContext request;
    ObjectMapper mapper = new ObjectMapper();

    public CustomerAPI(APIRequestContext request) {
        this.request = request;
    }

    //POST requests
    public String sendPostRequestToUpdateCustomer(int customerId, User user) {
        APIResponse response = request.post(String.format("customers/update/" + buildCustomerArguments(customerId, user),
                customerId));
        if (!response.ok()) {
            throw new IllegalStateException("Could not update customer " + customerId);
        }
        return response.text();
    }

    //GET request
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

    public CustomerDto sendGetRequestForCustomerDetails(int customerId) {
        APIResponse response = request.get(String.format("customers/%d", customerId));
        if (!response.ok()) {
            throw new IllegalStateException("Failed to get customer details through GET request");
        }
        try {
            return mapper.readValue(response.text(), CustomerDto.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse customer details from GET request");
        }
    }

    //Helpers

    public String buildCustomerArguments(int customerId, User user) {
        return String.format(
                "%d?firstName=%s&lastName=%s&street=%s&city=%s&state=%s&zipCode=%s&phoneNumber=%s&ssn=%s&username=%s&password=%s",
                customerId,
                user.firstName(),
                user.lastName(),
                user.address(),
                user.city(),
                user.state(),
                user.zipCode(),
                user.phoneNumber(),
                user.ssn(),
                user.username(),
                user.password()
        );
    }


}
