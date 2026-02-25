package com.example.qa.tests.test_data.test_data_factories;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.api.helpers.ApiHelper;
import com.example.qa.enums.AccountType;
import com.example.qa.tests.test_data.test_data_records.NewAccountsForTests;
import com.microsoft.playwright.APIRequestContext;

public class AccountsDataFactory {

    private final AccountActionsAPI accountActionsAPI;
    private final ApiHelper beHelper;

    public AccountsDataFactory(APIRequestContext request) {
        accountActionsAPI = new AccountActionsAPI(request);
        beHelper = new ApiHelper(request);
    }

    public NewAccountsForTests createOneNewAccountForTest(
            int customerId,
            int originalAccountId,
            AccountType accountType
    ) {
        AccountDto newAccount = accountActionsAPI.createNewAccount(
                customerId,
                accountType,
                originalAccountId
        );
        return new NewAccountsForTests(
                newAccount,
                beHelper.retrieveAccountBalance(customerId, newAccount.id()),
                null,
                null
        );
    }

    public NewAccountsForTests createTwoNewAccountsForTest(
            int customerId,
            int originalAccountId,
            AccountType accountType1,
            AccountType accountType2
    ) {
        AccountDto newAccount1 = accountActionsAPI.createNewAccount(
                customerId,
                accountType1,
                originalAccountId
        );
        AccountDto newAccount2 = accountActionsAPI.createNewAccount(
                customerId,
                accountType2,
                originalAccountId
        );
        return new NewAccountsForTests(
                newAccount1,
                beHelper.retrieveAccountBalance(customerId, newAccount1.id()),
                newAccount2,
                beHelper.retrieveAccountBalance(customerId, newAccount2.id())
        );
    }
}
