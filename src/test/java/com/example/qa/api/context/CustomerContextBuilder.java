package com.example.qa.api.context;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.clients.CustomerAPI;
import com.example.qa.models.User;
import com.microsoft.playwright.APIRequestContext;

public class CustomerContextBuilder {

    private final CustomerAPI customerRequests;
    private final AccountActionsAPI accountRequests;

    public CustomerContextBuilder(APIRequestContext request) {
        this.customerRequests = new CustomerAPI(request);
        this.accountRequests = new AccountActionsAPI(request);
    }

    public CustomerContext buildContextFor(User user) {
        int customerId;
        int originalAccountId;
        customerId = customerRequests.sendGetRequestToLogIn(user.getUsername(), user.getPassword()).getId();
        originalAccountId = accountRequests.sendGetRequestForCustomerAccountsInfo(customerId).get(0).getId();
        return new CustomerContext(customerId, originalAccountId);
    }

}
