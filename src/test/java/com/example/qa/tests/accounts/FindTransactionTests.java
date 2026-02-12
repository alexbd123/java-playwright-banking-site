package com.example.qa.tests.accounts;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.api.dtos.TransactionDto;
import com.example.qa.enums.AccountTypes;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import com.example.qa.tests.utils.TimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FindTransactionTests extends AuthenticatedBaseTest {

    protected static AccountActionsAPI accountActionsAPI;
    protected AccountDto originalCheckingAccount;
    protected static TimeUtils time;

    @BeforeAll
    void initApiAndGetContext() {
        accountActionsAPI = new AccountActionsAPI(request);
        originalCheckingAccount = customerContext.getOriginalAccount();
        time = new TimeUtils();
    }

    @ParameterizedTest(name = "{2} of {0} from {1} account can be found by date")
    @MethodSource("provideWithdrawalAmountAndAccountType")
    void userCanFindWithdrawTransactionsByDate(BigDecimal amountToWithdraw, AccountTypes accountType, String amountDescription) {
        int accountId = accountType == AccountTypes.CHECKING ? originalCheckingAccount.id() :
                accountActionsAPI.createNewAccount(
                customerContext.getCustomerId(),
                accountType,
                originalCheckingAccount.id()).id();
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

    private static Stream<Arguments> provideWithdrawalAmountAndAccountType() {
        return Stream.of(
                Arguments.of(new BigDecimal("10.00"), AccountTypes.CHECKING, "Small withdrawal"),
                Arguments.of(new BigDecimal("150.00"), AccountTypes.CHECKING, "Medium withdrawal"),
                Arguments.of(new BigDecimal("10000.00"), AccountTypes.CHECKING, "Large withdrawal"),
                Arguments.of(new BigDecimal("10.00"), AccountTypes.SAVINGS, "Small withdrawal"),
                Arguments.of(new BigDecimal("150.00"), AccountTypes.SAVINGS, "Medium withdrawal"),
                Arguments.of(new BigDecimal("10000.00"), AccountTypes.SAVINGS, "Large withdrawal")
        );
    }
}
