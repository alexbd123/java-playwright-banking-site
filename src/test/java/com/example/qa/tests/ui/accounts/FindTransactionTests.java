package com.example.qa.tests.ui.accounts;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.TransactionDto;
import com.example.qa.enums.AccountTypes;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import com.example.qa.tests.test_data.TransactionDataFactory;
import com.example.qa.tests.test_data.TransferTransactionData;
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
    protected int originalAccountId;
    protected int originalCustomerId;
    protected TimeUtils time;
    protected TransactionDataFactory transactionDataFactory;

    @BeforeAll
    void initApiAndGetContext() {
        accountActionsAPI = new AccountActionsAPI(request);
        originalAccountId = customerContext.originalAccount().id();
        originalCustomerId = customerContext.customerId();
        time = new TimeUtils();
        transactionDataFactory = new TransactionDataFactory(request);
    }

    //Find withdrawals

    @ParameterizedTest(name = "{0} withdrawal of {1} from {2} account can be found by date")
    @MethodSource("provideAmountAndAccountType")
    void userCanFindWithdrawalByDate(String amountDescription, BigDecimal amountToWithdraw, AccountTypes accountType) {
        //Arrange: Use test data class to create test data
        WithdrawalTransactionData testData = transactionDataFactory.buildTestDataForWithdrawal(
                amountToWithdraw,
                accountType,
                originalCustomerId,
                originalAccountId
        );

        //Act: Use UI to find transaction by date, verify amount and type is correct
        goTo.findTransactions();
        findTransactionsPage.findTransactionByDate(testData.accountId(), testData.transactionDate());
        findTransactionsPage.verifyTransactionTypeAndAmountInTable(testData.expectedTransaction().type(), testData.expectedTransaction().id(), amountToWithdraw);
        findTransactionsPage.goToTransactionDetails(testData.expectedTransaction().id());
        TransactionDto actualTransaction = transactionDetailsPage.toDto(testData.accountId());

        //Assert: Verify actual transaction from UI matches expected transaction dto
        Assertions.assertEquals(testData.expectedTransaction(), actualTransaction);
    }

    @ParameterizedTest(name = "{0} withdrawal of {1} from {2} account can be found by amount")
    @MethodSource("provideAmountAndAccountType")
    void userCanFindWithdrawalByAmount(String amountDescription, BigDecimal amountToWithdraw, AccountTypes accountType) {
        //Arrange: Use test data class to create test data
        WithdrawalTransactionData testData = transactionDataFactory.buildTestDataForWithdrawal(
                amountToWithdraw,
                accountType,
                originalCustomerId,
                originalAccountId
        );

        //Act: Use UI to find transaction by amount, verify amount and type is correct
        goTo.findTransactions();
        findTransactionsPage.findTransactionByAmount(testData.accountId(), amountToWithdraw);
        findTransactionsPage.verifyTransactionTypeAndAmountInTable(testData.expectedTransaction().type(), testData.expectedTransaction().id(), amountToWithdraw);
        findTransactionsPage.goToTransactionDetails(testData.expectedTransaction().id());
        TransactionDto actualTransaction = transactionDetailsPage.toDto(testData.accountId());

        //Assert: Verify actual transaction from UI matches expected transaction dto
        Assertions.assertEquals(testData.expectedTransaction(), actualTransaction);
    }

    //Find transfers

    @ParameterizedTest(name = "{0} transfer of {1} to {2} account can be found by amount")
    @MethodSource("provideAmountAndAccountType")
    void userCanFindTransferToByAmount(String amountDescription, BigDecimal transferAmount, AccountTypes accountType) {
        //Arrange: Create transfer test data using data builder
        TransferTransactionData testData = transactionDataFactory.buildTestDataForTransfer(
                transferAmount,
                accountType,
                originalCustomerId,
                originalAccountId,
                true,
                true
        );

        //Act: Use UI to find transaction by amount, verify amount and type is correct
        goTo.findTransactions();
        findTransactionsPage.findTransactionByAmount(testData.accountId(), transferAmount);
        findTransactionsPage.verifyTransactionTypeAndAmountInTable(testData.expectedTransaction().type(), testData.expectedTransaction().id(), transferAmount);
        findTransactionsPage.goToTransactionDetails(testData.expectedTransaction().id());
        TransactionDto actualTransaction = transactionDetailsPage.toDto(testData.accountId());

        //Assert: Verify actual transaction from UI matches expected transaction dto
        Assertions.assertEquals(testData.expectedTransaction(), actualTransaction);
    }

    @ParameterizedTest(name = "{0} transfer of {1} from {2} account can be found by amount")
    @MethodSource("provideAmountAndAccountType")
    void userCanFindTransferFromByAmount(String amountDescription, BigDecimal transferAmount, AccountTypes accountType) {
        //Arrange: Create transfer test data using data builder
        TransferTransactionData testData = transactionDataFactory.buildTestDataForTransfer(
                transferAmount,
                accountType,
                originalCustomerId,
                originalAccountId,
                true,
                false
        );

        //Act: Use UI to find transaction by amount, verify amount and type is correct
        goTo.findTransactions();
        findTransactionsPage.findTransactionByAmount(testData.accountId(), transferAmount);
        findTransactionsPage.verifyTransactionTypeAndAmountInTable(testData.expectedTransaction().type(), testData.expectedTransaction().id(), transferAmount);
        findTransactionsPage.goToTransactionDetails(testData.expectedTransaction().id());
        TransactionDto actualTransaction = transactionDetailsPage.toDto(testData.accountId());

        //Assert: Verify actual transaction from UI matches expected transaction dto
        Assertions.assertEquals(testData.expectedTransaction(), actualTransaction);
    }

    private static Stream<Arguments> provideAmountAndAccountType() {
        return Stream.of(
                Arguments.of( "Small", new BigDecimal("10.00"), AccountTypes.CHECKING),
                Arguments.of("Medium", new BigDecimal("150.00"), AccountTypes.CHECKING),
                Arguments.of("Large", new BigDecimal("10000.00"), AccountTypes.CHECKING),
                Arguments.of("Small", new BigDecimal("10.00"), AccountTypes.SAVINGS),
                Arguments.of("Medium", new BigDecimal("150.00"), AccountTypes.SAVINGS),
                Arguments.of("Large", new BigDecimal("10000.00"), AccountTypes.SAVINGS)
        );
    }
}
