package com.example.qa.tests.test_data.test_data_factories;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.TransactionDto;
import com.example.qa.enums.AccountTypes;
import com.example.qa.tests.test_data.test_data_records.WithdrawalTransactionData;
import com.example.qa.tests.utils.TimeUtils;
import com.microsoft.playwright.APIRequestContext;

import java.math.BigDecimal;
import java.util.List;

public class WithdrawalsDataFactory {

    private final AccountActionsAPI accountActionsAPI;
    private final TimeUtils time;

    public WithdrawalsDataFactory(APIRequestContext request) {
        this.accountActionsAPI = new AccountActionsAPI(request);
        this.time = new TimeUtils();
    }

    public WithdrawalTransactionData buildTestDataForWithdrawalFromNewAccount(
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

}
