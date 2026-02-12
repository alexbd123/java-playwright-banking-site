package com.example.qa.api.clients;

import com.example.qa.api.dtos.AccountDto;
import com.example.qa.api.dtos.TransactionDto;
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

    //POST
    public AccountDto createNewAccount(
            Integer customerId,
            AccountTypes accountType,
            int fromAccountId) {
        Integer accountTypeInt = (accountType == AccountTypes.CHECKING) ? 0 : 1;
        APIResponse response = request.post(String.format("createAccount?customerId=%d&newAccountType=%d&fromAccountId=%d",
                customerId,
                accountTypeInt,
                fromAccountId));
        if (!response.ok()) {
            throw new IllegalStateException("Account creation failed");
        }
        try {
            return mapper.readValue(response.text(), AccountDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse account response", e);
        }
    }

    public void sendPostRequestToDepositFunds(int intoAccountId, BigDecimal amount) {
        APIResponse response = request.post(String.format(
                "deposit?accountId=%d&amount=%.2f",
                intoAccountId,
                amount));
        if (!response.ok()) {
            throw new IllegalStateException("Failed to deposit funds");
        }
    }

    public String sendPostRequestToTransferFunds(int fromAccountId, int toAccountId, BigDecimal amount) {
        APIResponse response = request.post(String.format(
                "transfer?fromAccountId=%d&toAccountId=%d&amount=%.2f",
                fromAccountId,
                toAccountId, amount));
        if (!response.ok()) {
            throw new IllegalStateException("Failed to transfer funds");
        }
        return response.text();
    }

    public void sendPostRequestToWithdrawFunds(int fromAccountId, BigDecimal amount) {
        APIResponse response = request.post(String.format("withdraw?accountId=%d&amount=%.2f", fromAccountId, amount));
        if (!response.ok()) {
            throw new IllegalStateException("Failed to withdraw funds from account " + fromAccountId);
        }
    }

    //GET
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

    public TransactionDto sendGetRequestToRetrieveTransactionByAmount(Integer accountId, BigDecimal amount) {
        APIResponse response = request.get(String.format("accounts/%d/transactions/amount/%.2f", accountId, amount));
        if (!response.ok()) {
            throw new IllegalStateException("Failed to get retrieve transaction by amount");
        }
        try {
            return mapper.readValue(response.text(), TransactionDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse transaction response", e);
        }
    }

    public List<TransactionDto> sendGetRequestForAllTransactionsForAccount(Integer accountId) {
        APIResponse response = request.get(String.format("accounts/%d/transactions", accountId));
        if (!response.ok()) {
            throw new IllegalStateException("Failed to get all transactions for account " + accountId);
        }
        try {
            return mapper.readValue(response.text(), new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse all transactions response for account " + accountId, e);
        }
    }
}
