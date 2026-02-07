package com.example.qa.api.clients;

import com.example.qa.api.dtos.AccountDto;
import com.example.qa.enums.AccountTypes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

import java.math.BigDecimal;
import java.util.List;

public class AccountActionsAPI {

    private final APIRequestContext request;
    ObjectMapper mapper = new ObjectMapper();

    public AccountActionsAPI(APIRequestContext request) {
        this.request = request;
    }

    //HTTP methods
    public AccountDto createNewAccount(
            Integer customerId,
            AccountTypes accountType,
            AccountDto fromAccount) {
        Integer accountTypeInt = (accountType == AccountTypes.CHECKING) ? 0 : 1;
        APIResponse response = request.post(String.format("createAccount?customerId=%d&newAccountType=%d&fromAccountId=%d",
                customerId,
                accountTypeInt,
                fromAccount.getId()));
        if (!response.ok()) {
            throw new IllegalStateException("Account creation failed");
        }
        try {
            return mapper.readValue(response.text(), AccountDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse account response", e);
        }
    }

    public List<AccountDto> sendGetRequestForCustomerAccountsInfo(Integer customerId) {
        APIResponse response = request.get(String.format("customers/%s/accounts", customerId));
        if (!response.ok()) {
            throw new IllegalStateException("Failed to get customer account info");
        }
        try {
            return mapper.readValue(response.text(), new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to get customer account info", e);
        }
    }

    public void sendPostRequestToDepositFunds(AccountDto intoAccount, BigDecimal amount) {
        APIResponse response = request.post(String.format(
                "deposit?accountId=%d&amount=%.2f",
                intoAccount.getId(),
                amount));
        if (!response.ok()) {
            throw new IllegalStateException("Failed to deposit funds");
        }
    }

    public void sendPostRequestToTransferFunds(int fromAccountId, int toAccountId, BigDecimal amount) {
        APIResponse response = request.post(String.format(
                "transfer?fromAccountId=%d&toAccountId=%d&amount=%.2f",
                fromAccountId,
                toAccountId, amount));
        if (!response.ok()) {
            throw new IllegalStateException("Failed to transfer funds");
        }
    }

    public AccountDto getAccountById(Integer accountId) {
        APIResponse response = request.get(String.format("accounts/%d", accountId));
        if (!response.ok()) {
            throw new IllegalStateException("Failed to get account info");
        }
        try {
            return mapper.readValue(response.text(), AccountDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse account response", e);
        }
    }
}
