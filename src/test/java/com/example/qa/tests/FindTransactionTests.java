package com.example.qa.tests;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.api.dtos.TransactionDto;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import com.example.qa.tests.utils.TimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FindTransactionTests extends AuthenticatedBaseTest {

    protected static AccountActionsAPI accountActionsAPI;
    protected AccountDto originalCheckingAccount;
    protected static TimeUtils time;

    @BeforeEach
    void initApiClients() {
        accountActionsAPI = new AccountActionsAPI(request);
        originalCheckingAccount = customerContext.getOriginalAccount();
        time = new TimeUtils();
    }

    @Test
    void userCanFindTransactionByDate() {
        int accountId = originalCheckingAccount.id();
        BigDecimal amountToWithdraw = new BigDecimal("100.00");
        accountActionsAPI.sendPostRequestToWithdrawFunds(accountId, amountToWithdraw);
        List<TransactionDto> transactionsForAccount = accountActionsAPI.sendGetRequestForAllTransactionsForAccount(accountId);
        TransactionDto expectedTransaction = transactionsForAccount.stream()
                .filter(t -> t.amount().equals(amountToWithdraw))
                .findFirst()
                .orElseThrow();
        int transactionId = expectedTransaction.id();
        String transactionDate = time.convertUnixToUIDate(expectedTransaction.date());

        goTo.findTransactions();
        findTransactionsPage.findTransactionByDate(accountId, transactionId, transactionDate);
        TransactionDto actualTransaction = transactionDetailsPage.toDto(accountId);

        Assertions.assertEquals(expectedTransaction, actualTransaction);
    }
}
