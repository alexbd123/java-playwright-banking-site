package com.example.qa.tests.test_data;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.api.dtos.TransactionDto;
import com.example.qa.api.helpers.ApiHelper;
import com.example.qa.enums.AccountTypes;
import com.example.qa.tests.utils.TimeUtils;
import com.microsoft.playwright.APIRequestContext;

import java.math.BigDecimal;
import java.util.List;

public class TransactionDataFactory {

    private final AccountActionsAPI accountActionsAPI;
    private final TimeUtils time;
    private final ApiHelper beHelper;

    public TransactionDataFactory(APIRequestContext request) {
        accountActionsAPI = new AccountActionsAPI(request);
        time = new TimeUtils();
        beHelper = new ApiHelper(request);
    }

    public WithdrawalTransactionData buildTestDataForWithdrawal(
            BigDecimal amountToWithdraw,
            AccountTypes accountType,
            int customerId,
            int originalCheckingAccountId
    ) {
        //Get correct account ID
        int accountId = accountType == AccountTypes.CHECKING ? originalCheckingAccountId :
                accountActionsAPI.createNewAccount(
                        customerId,
                        accountType,
                        originalCheckingAccountId
                ).id();

        //Withdrawal via API
        accountActionsAPI.sendPostRequestToWithdrawFunds(accountId, amountToWithdraw);

        //Get transaction DTO
        List<TransactionDto> transactionsForAccount = accountActionsAPI.sendGetRequestForAllTransactionsForAccount(accountId);
        TransactionDto expectedTransaction = transactionsForAccount.stream()
                .filter(t -> t.amount().equals(amountToWithdraw))
                .findFirst()
                .orElseThrow();

        String transactionDate = time.convertUnixToUIDate(expectedTransaction.date());

        //return test data
        return new WithdrawalTransactionData(accountId, expectedTransaction, transactionDate);
    }

    public TransferTransactionData buildTestDataForTransfer(
            BigDecimal transferAmount,
            AccountTypes accountType,
            int customerId,
            int originalAccountId,
            boolean doTransaction,
            boolean transferToNewAccount
    ) {
        int newAccountId = accountActionsAPI.createNewAccount(
                customerId,
                accountType,
                originalAccountId).id();
        BigDecimal accountBalance = beHelper.retrieveAccountBalance(customerId, newAccountId);
        if (!doTransaction) {
            return new TransferTransactionData(newAccountId, accountBalance, null);
        } else {
            int fromAccountId = transferToNewAccount ? originalAccountId : newAccountId;
            int toAccountId = transferToNewAccount ? newAccountId : originalAccountId;
            List<TransactionDto> transactionsBeforeTransfer = accountActionsAPI.sendGetRequestForAllTransactionsForAccount(newAccountId);
            accountActionsAPI.sendPostRequestToTransferFunds(fromAccountId, toAccountId, transferAmount);
            TransactionDto expectedTransaction = beHelper.determineNewTransactionFromList(transactionsBeforeTransfer, newAccountId);
            return new TransferTransactionData(newAccountId, accountBalance, expectedTransaction);
        }
    }
}
