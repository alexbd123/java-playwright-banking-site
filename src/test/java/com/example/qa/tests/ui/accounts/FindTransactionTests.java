package com.example.qa.tests.ui.accounts;

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

    @ParameterizedTest(name = "{0} of {1} from {2} account can be found by date")
    @MethodSource("provideWithdrawalAmountAndAccountType")
    void userCanFindWithdrawalByDate(String amountDescription, BigDecimal amountToWithdraw, AccountTypes accountType) {
        //Arrange: Create correct account type, withdraw funds via API, set up expected transaction dto
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

        //Act: Use UI to find transaction by date
        goTo.findTransactions();
        findTransactionsPage.findTransactionByDate(accountId, transactionId, transactionDate);
        TransactionDto actualTransaction = transactionDetailsPage.toDto(accountId);

        //Assert: Verify actual transaction from UI matches expected transaction dto
        Assertions.assertEquals(expectedTransaction, actualTransaction);
    }

    @ParameterizedTest(name = "{0} of {1} from {2} account can be found by amount")
    @MethodSource("provideWithdrawalAmountAndAccountType")
    void userCanFindWithdrawalByAmount(String amountDescription, BigDecimal amountToWithdraw, AccountTypes accountType) {
        //Arrange: Create correct account type, withdraw funds via API, set up expected transaction dto
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

        //Act: Use UI to find transaction by amount
        goTo.findTransactions();
        findTransactionsPage.findTransactionByAmount(accountId, transactionId, amountToWithdraw);
        TransactionDto actualTransaction = transactionDetailsPage.toDto(accountId);

        //Assert: Verify actual transaction from UI matches expected transaction dto
        Assertions.assertEquals(expectedTransaction, actualTransaction);
    }

    private static Stream<Arguments> provideWithdrawalAmountAndAccountType() {
        return Stream.of(
                Arguments.of( "Small withdrawal", new BigDecimal("10.00"), AccountTypes.CHECKING),
                Arguments.of("Medium withdrawal", new BigDecimal("150.00"), AccountTypes.CHECKING),
                Arguments.of("Large withdrawal", new BigDecimal("10000.00"), AccountTypes.CHECKING),
                Arguments.of("Small withdrawal", new BigDecimal("10.00"), AccountTypes.SAVINGS),
                Arguments.of("Medium withdrawal", new BigDecimal("150.00"), AccountTypes.SAVINGS),
                Arguments.of("Large withdrawal", new BigDecimal("10000.00"), AccountTypes.SAVINGS)
        );
    }
}
