package com.example.qa.api.helpers;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.microsoft.playwright.APIRequestContext;

import java.math.BigDecimal;

public class ApiHelper {

    private final AccountActionsAPI accountActionsAPI;

    public ApiHelper(APIRequestContext request) {
        this.accountActionsAPI = new AccountActionsAPI(request);
    }

    public BigDecimal retrieveAccountBalance(int customerId, int accountId) {
        return accountActionsAPI.sendGetRequestForCustomerAccountsInfo(
                        customerId).stream()
                .filter(a -> a.id() == accountId)
                .map(AccountDto::balance)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

}