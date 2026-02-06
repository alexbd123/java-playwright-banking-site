package com.example.qa.api.context;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.clients.CustomerAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.models.User;
import com.microsoft.playwright.APIRequestContext;

import java.math.BigDecimal;

public class CustomerContextBuilder {

    private final CustomerAPI customerRequests;
    private final AccountActionsAPI accountRequests;

    public CustomerContextBuilder(APIRequestContext request) {
        this.customerRequests = new CustomerAPI(request);
        this.accountRequests = new AccountActionsAPI(request);
    }

    public CustomerContext buildContextFor(User user) {
        int customerId;
        AccountDto originalAccount;
        customerId = customerRequests.sendGetRequestToLogIn(user.getUsername(), user.getPassword()).getId();
        originalAccount = accountRequests.sendGetRequestForCustomerAccountsInfo(customerId).get(0);
        return new CustomerContext(customerId, originalAccount);
    }

}
