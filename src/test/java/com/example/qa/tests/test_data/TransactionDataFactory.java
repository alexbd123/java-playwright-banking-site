package com.example.qa.tests.test_data;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.api.dtos.TransactionDto;
import com.example.qa.enums.AccountTypes;
import com.example.qa.tests.utils.TimeUtils;
import com.microsoft.playwright.APIRequestContext;

import java.math.BigDecimal;
import java.util.List;

public class TransactionDataFactory {

    private final AccountActionsAPI accountActionsAPI;
    private final TimeUtils time;

    public TransactionDataFactory(APIRequestContext request) {
        accountActionsAPI = new AccountActionsAPI(request);
        time = new TimeUtils();
    }

    public WithdrawalTransactionData buildTestDataForWithdrawal(
            BigDecimal amountToWithdraw,
            AccountTypes accountType,
            int customerId,
            AccountDto originalCheckingAccount
    ) {
        //Get correct account ID
        int accountId = accountType == AccountTypes.CHECKING ? originalCheckingAccount.id() :
                accountActionsAPI.createNewAccount(
                        customerId,
                        accountType,
                        originalCheckingAccount.id()).id();

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
}
