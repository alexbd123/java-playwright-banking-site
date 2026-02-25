package com.example.qa.tests.test_data.test_data_factories;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.TransactionDto;
import com.example.qa.api.helpers.ApiHelper;
import com.example.qa.enums.AccountType;
import com.example.qa.tests.test_data.test_data_records.TransferTransactionData;
import com.example.qa.tests.utils.TimeUtils;
import com.microsoft.playwright.APIRequestContext;

import java.math.BigDecimal;
import java.util.List;

public class TransfersDataFactory {

    private final AccountActionsAPI accountActionsAPI;
    private final TimeUtils time;
    private final ApiHelper beHelper;

    public TransfersDataFactory(APIRequestContext request) {
        accountActionsAPI = new AccountActionsAPI(request);
        time = new TimeUtils();
        beHelper = new ApiHelper(request);
    }

    public TransferTransactionData buildTestDataForTransferToNewAccount(
            BigDecimal transferAmount,
            AccountType accountType,
            int customerId,
            int originalAccountId
    ) {
        int newAccountId = accountActionsAPI.createNewAccount(
                customerId,
                accountType,
                originalAccountId).id();
        BigDecimal accountBalance = beHelper.retrieveAccountBalance(customerId, newAccountId);
        List<TransactionDto> transactionsBeforeTransfer = accountActionsAPI.sendGetRequestForAllTransactionsForAccount(newAccountId);
        accountActionsAPI.sendPostRequestToTransferFunds(originalAccountId, newAccountId, transferAmount);
        TransactionDto expectedTransaction = beHelper.determineNewTransactionFromList(transactionsBeforeTransfer, newAccountId);
        String transactionDate = time.convertUnixToUIDate(expectedTransaction.date());
        return new TransferTransactionData(newAccountId, accountBalance, expectedTransaction, transactionDate);
    }

    public TransferTransactionData buildTestDataForTransferFromNewAccount(
            BigDecimal transferAmount,
            AccountType accountType,
            int customerId,
            int originalAccountId
    ) {
        int newAccountId = accountActionsAPI.createNewAccount(
                customerId,
                accountType,
                originalAccountId).id();
        BigDecimal accountBalance = beHelper.retrieveAccountBalance(customerId, newAccountId);
        List<TransactionDto> transactionsBeforeTransfer = accountActionsAPI.sendGetRequestForAllTransactionsForAccount(newAccountId);
        accountActionsAPI.sendPostRequestToTransferFunds(newAccountId, originalAccountId, transferAmount);
        TransactionDto expectedTransaction = beHelper.determineNewTransactionFromList(transactionsBeforeTransfer, newAccountId);
        String transactionDate = time.convertUnixToUIDate(expectedTransaction.date());
        return new TransferTransactionData(newAccountId, accountBalance, expectedTransaction, transactionDate);
    }
}
