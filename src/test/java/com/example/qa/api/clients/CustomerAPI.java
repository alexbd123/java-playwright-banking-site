package com.example.qa.api.clients;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

public class CustomerAPI {

    private final APIRequestContext request;

    public CustomerAPI(APIRequestContext request) {
        this.request = request;
    }

    public APIResponse sendGetRequestToLogIn(String username, String password) {
        return request.get(String.format("login/%s/%s", username, password));
    }

    public APIResponse sendGetRequestForCustomerAccountsInfo(Integer customerId) {
        return request.get(String.format("customers/%s/accounts", customerId));
    }
}
