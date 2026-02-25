package com.example.qa.api.clients;

import com.example.qa.api.dtos.AccountDto;
import com.example.qa.api.dtos.TransactionDto;
import com.example.qa.api.http.HTTPRequests;
import com.example.qa.api.http.RequestsFactory;
import com.example.qa.enums.AccountType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

import java.math.BigDecimal;
import java.util.List;

public class AccountActionsAPI {

    private final APIRequestContext request;
    private final HTTPRequests http;
    private final RequestsFactory requestsFactory;
    ObjectMapper mapper = new ObjectMapper();

    public AccountActionsAPI(APIRequestContext request) {
        this.request = request;
        http = new HTTPRequests(request);
        requestsFactory = new RequestsFactory();
    }

    //HTTP methods

    //POST
    public AccountDto createNewAccount(
            Integer customerId,
            AccountType accountType,
            int fromAccountId
    ) {
        APIResponse response = http.post("createAccount",
                requestsFactory.buildCreateNewAccountRequest(customerId,
                        accountType,
                        fromAccountId)
        );
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
        APIResponse response = http.post(
                "transfer",
                requestsFactory.buildTransferFundsRequests(
                        fromAccountId,
                        toAccountId,
                        amount));
        if (!response.ok()) {
            throw new IllegalStateException("Failed to transfer funds");
        }
        return response.text();
    }

    public String sendPostRequestToWithdrawFunds(int fromAccountId, BigDecimal amount) {
        APIResponse response = http.post("withdraw", requestsFactory.buildWithdrawFundsRequest(fromAccountId, amount));
        if (!response.ok()) {
            throw new IllegalStateException("Failed to withdraw funds from account " + fromAccountId);
        }
        return response.text();
    }

    //GET
    public List<AccountDto> sendGetRequestForCustomerAccountsInfo(Integer customerId) {
        APIResponse response = http.get("customers", requestsFactory.buildRetrieveCustomerAccountsRequest(customerId));
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
            return mapper.readValue(response.text(), new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse all transactions response for account " + accountId, e);
        }
    }
}
