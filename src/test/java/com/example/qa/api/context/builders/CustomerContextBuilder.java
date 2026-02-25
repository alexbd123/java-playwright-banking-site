package com.example.qa.api.context.builders;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.clients.ApplicationParametersAPI;
import com.example.qa.api.clients.CustomerAPI;
import com.example.qa.api.context.CustomerContext;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.api.dtos.User;
import com.example.qa.config.TestConfig;
import com.microsoft.playwright.APIRequestContext;

import java.math.BigDecimal;

public class CustomerContextBuilder {

    private final CustomerAPI customerRequests;
    private final AccountActionsAPI accountRequests;
    private final ApplicationParametersAPI parametersAPI;

    public CustomerContextBuilder(APIRequestContext request) {
        this.customerRequests = new CustomerAPI(request);
        this.accountRequests = new AccountActionsAPI(request);
        this.parametersAPI = new ApplicationParametersAPI(request);
    }

    public CustomerContext buildContextFor(User user) {
        int customerId;
        AccountDto originalAccount;
        customerId = customerRequests.sendGetRequestToLogIn(user.username(), user.password()).id();
        originalAccount = accountRequests.sendGetRequestForCustomerAccountsInfo(customerId).get(0);
        parametersAPI.setInitialBalance(new BigDecimal(TestConfig.getInitialBalance()));
        return new CustomerContext(customerId, originalAccount);
    }

}
