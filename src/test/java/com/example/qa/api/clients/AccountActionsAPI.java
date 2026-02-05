package com.example.qa.api.clients;

import com.example.qa.enums.AccountTypes;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

public class AccountActionsAPI {

    private final APIRequestContext request;

    public AccountActionsAPI(APIRequestContext request) {
        this.request = request;
    }

    public APIResponse createNewAccount(Integer customerId, AccountTypes accountType, Integer fromAccountId) {
        Integer accountTypeInt = (accountType == AccountTypes.CHECKING) ? 0 : 1;
        return request.post(String.format("createAccount?customerId=%d&newAccountType=%d&fromAccountId=%d", customerId, accountTypeInt, fromAccountId));
    }

}
