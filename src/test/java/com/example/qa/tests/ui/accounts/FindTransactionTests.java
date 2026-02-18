package com.example.qa.tests.ui.accounts;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.api.dtos.TransactionDto;
import com.example.qa.enums.AccountTypes;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import com.example.qa.tests.test_data.TransactionDataFactory;
import com.example.qa.tests.test_data.WithdrawalTransactionData;
import com.example.qa.tests.utils.TimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FindTransactionTests extends AuthenticatedBaseTest {

    protected AccountActionsAPI accountActionsAPI;
    protected AccountDto originalCheckingAccount;
    protected int originalCustomerId;
    protected TimeUtils time;
    protected TransactionDataFactory transactionDataFactory;

    @BeforeAll
    void initApiAndGetContext() {
        accountActionsAPI = new AccountActionsAPI(request);
        originalCheckingAccount = customerContext.originalAccount();
        originalCustomerId = customerContext.customerId();
        time = new TimeUtils();
        transactionDataFactory = new TransactionDataFactory(request);
    }

    @ParameterizedTest(name = "{0} of {1} from {2} account can be found by date")
    @MethodSource("provideWithdrawalAmountAndAccountType")
    void userCanFindWithdrawalByDate(String amountDescription, BigDecimal amountToWithdraw, AccountTypes accountType) {
        //Arrange: Use test data class to create test data
        WithdrawalTransactionData testData = transactionDataFactory.buildTestDataForWithdrawal(
                amountToWithdraw,
                accountType,
                originalCustomerId,
                originalCheckingAccount
        );

        //Act: Use UI to find transaction by date
        goTo.findTransactions();
        findTransactionsPage.findTransactionByDate(testData.accountId(), testData.expectedTransaction().id(), testData.transactionDate());
        TransactionDto actualTransaction = transactionDetailsPage.toDto(testData.accountId());

        //Assert: Verify actual transaction from UI matches expected transaction dto
        Assertions.assertEquals(testData.expectedTransaction(), actualTransaction);
    }

    @ParameterizedTest(name = "{0} of {1} from {2} account can be found by amount")
    @MethodSource("provideWithdrawalAmountAndAccountType")
    void userCanFindWithdrawalByAmount(String amountDescription, BigDecimal amountToWithdraw, AccountTypes accountType) {
        //Arrange: Use test data class to create test data
        WithdrawalTransactionData testData = transactionDataFactory.buildTestDataForWithdrawal(
                amountToWithdraw,
                accountType,
                originalCustomerId,
                originalCheckingAccount
        );

        //Act: Use UI to find transaction by amount
        goTo.findTransactions();
        findTransactionsPage.findTransactionByAmount(testData.accountId(), testData.expectedTransaction().id(), amountToWithdraw);
        TransactionDto actualTransaction = transactionDetailsPage.toDto(testData.accountId());

        //Assert: Verify actual transaction from UI matches expected transaction dto
        Assertions.assertEquals(testData.expectedTransaction(), actualTransaction);
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
