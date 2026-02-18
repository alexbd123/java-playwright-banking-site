package com.example.qa.tests.ui.accounts;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.api.dtos.TransactionDto;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import com.example.qa.tests.test_data.TransactionDataFactory;
import com.example.qa.tests.test_data.TransferTransactionData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import com.example.qa.enums.AccountTypes;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransfersTests extends AuthenticatedBaseTest {

    protected static AccountActionsAPI accountActionsAPI;
    protected AccountDto originalCheckingAccount;
    protected int originalAccountId;
    protected int originalCustomerId;
    protected TransactionDataFactory transactionDataFactory;


    @BeforeAll
    void initApiAndGetContext() {
        accountActionsAPI = new AccountActionsAPI(request);
        originalCheckingAccount = customerContext.originalAccount();
        originalAccountId = originalCheckingAccount.id();
        originalCustomerId = customerContext.customerId();
        transactionDataFactory = new TransactionDataFactory(request);
    }

    @ParameterizedTest(name = "User can transfer {0} to {1} account")
    @MethodSource("provideTransferAmountAndAccountType")
    void userShouldBeAbleToTransferFundsAndBalanceIncreases(BigDecimal transferAmount, AccountTypes accountType) {
        //Arrange: Create transfer test data using data builder
        TransferTransactionData testData = transactionDataFactory.buildTestDataForTransfer(
                transferAmount,
                accountType,
                originalCustomerId,
                originalAccountId,
                false,
                false
        );

        //Act: navigate to the Transfer Funds page, transfer to new account via UI, and verify correct UI message after transaction
        goTo.transferFunds();
        transferFundsPage.transferFunds(originalAccountId, testData.accountId(), transferAmount);
        transferFundsPage.assertAmountResultIsTransferAmount(transferAmount);

        //Assert: Verify that new account's id and balance are visible in overview, and that balance is correct after transfer
        BigDecimal expectedBalanceAfterTransfer = testData.originalBalance().add(transferAmount);
        feHelper.goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(testData.accountId(), expectedBalanceAfterTransfer);
    }

    @ParameterizedTest(name = "User can transfer {0} from {1} account and balance correctly decreases")
    @MethodSource("provideTransferAmountAndAccountType")
    void userShouldBeAbleToTransferFundsAndBalanceDecreases(BigDecimal transferAmount, AccountTypes accountType) {
        //Arrange: Create transfer test data using data builder
        TransferTransactionData testData = transactionDataFactory.buildTestDataForTransfer(
                transferAmount,
                accountType,
                originalCustomerId,
                originalAccountId,
                false,
                false
        );

        //Act: navigate to the Transfer Funds page, transfer to new account via UI, and verify correct UI message after transaction
        goTo.transferFunds();
        transferFundsPage.transferFunds(testData.accountId(), originalAccountId, transferAmount);
        transferFundsPage.assertAmountResultIsTransferAmount(transferAmount);

        //Assert: Verify that new account's id and balance are visible in overview, and that balance is correct after transfer
        BigDecimal expectedBalanceAfterTransfer = testData.originalBalance().subtract(transferAmount);
        feHelper.goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(testData.accountId(), expectedBalanceAfterTransfer);
    }

    @ParameterizedTest(name = "Transaction of {0} to {1} account creates correct credit transaction record")
    @MethodSource("provideTransferAmountAndAccountType")
    void transactionCreatesCorrectCreditTransactionRecord(BigDecimal transferAmount, AccountTypes accountType) {
        //Arrange: Create transfer test data using data builder
        TransferTransactionData testData = transactionDataFactory.buildTestDataForTransfer(
                transferAmount,
                accountType,
                originalCustomerId,
                originalAccountId,
                true,
                true
        );

        //Act: Navigate to the transaction details page and grab actual transaction from the UI
        goTo.transactionDetailsPage(testData.expectedTransaction().id());
        TransactionDto actualTransaction = transactionDetailsPage.toDto(testData.accountId());

        //Assert: Equality comparison between expected and actual transaction to verify transaction record's correctness
        Assertions.assertEquals(testData.expectedTransaction(), actualTransaction);
    }

    @ParameterizedTest(name = "Transaction of {0} from {1} account creates correct debit transaction record")
    @MethodSource("provideTransferAmountAndAccountType")
    void transactionCreatesCorrectDebitTransactionRecord(BigDecimal transferAmount, AccountTypes accountType) {
        //Arrange: Create transfer test data using data builder
        TransferTransactionData testData = transactionDataFactory.buildTestDataForTransfer(
                transferAmount,
                accountType,
                originalCustomerId,
                originalAccountId,
                true,
                false
        );

        //Act: Navigate to the transaction details page and grab actual transaction from the UI
        goTo.transactionDetailsPage(testData.expectedTransaction().id());
        TransactionDto actualTransaction = transactionDetailsPage.toDto(testData.accountId());

        //Assert: Equality comparison between expected and actual transaction to verify transaction record's correctness
        Assertions.assertEquals(testData.expectedTransaction(), actualTransaction);
    }

    //Method sources

    private static Stream<Arguments> provideTransferAmountAndAccountType() {
        return Stream.of(
                Arguments.of(new BigDecimal("5.00"), AccountTypes.CHECKING),
                Arguments.of(new BigDecimal("150.00"), AccountTypes.CHECKING),
                Arguments.of(new BigDecimal("10000.00"), AccountTypes.CHECKING),
                Arguments.of(new BigDecimal("5.00"), AccountTypes.SAVINGS),
                Arguments.of(new BigDecimal("150.00"), AccountTypes.SAVINGS),
                Arguments.of(new BigDecimal("10000.00"), AccountTypes.SAVINGS)
        );
    }

}
