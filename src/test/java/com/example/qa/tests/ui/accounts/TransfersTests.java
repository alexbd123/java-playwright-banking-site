package com.example.qa.tests.ui.accounts;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.api.dtos.TransactionDto;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import com.example.qa.tests.test_data.test_data_factories.AccountsDataFactory;
import com.example.qa.tests.test_data.test_data_records.NewAccountsForTests;
import com.example.qa.tests.test_data.test_data_factories.TransfersDataFactory;
import com.example.qa.tests.test_data.test_data_records.TransferTransactionData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import com.example.qa.enums.AccountType;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransfersTests extends AuthenticatedBaseTest {

    protected static AccountActionsAPI accountActionsAPI;
    protected AccountDto originalCheckingAccount;
    protected int originalAccountId;
    protected int originalCustomerId;
    protected TransfersDataFactory transfersDataFactory;
    protected AccountsDataFactory accountsDataFactory;


    @BeforeAll
    void initApiAndGetContext() {
        accountActionsAPI = new AccountActionsAPI(request);
        originalCheckingAccount = customerContext.originalAccount();
        originalAccountId = originalCheckingAccount.id();
        originalCustomerId = customerContext.customerId();
        transfersDataFactory = new TransfersDataFactory(request);
        accountsDataFactory = new AccountsDataFactory(request);
    }

    @ParameterizedTest(name = "User can transfer {0} to {1} account")
    @MethodSource("provideTransferAmountAndAccountType")
    void userShouldBeAbleToTransferFundsAndBalanceIncreases(BigDecimal transferAmount, AccountType accountType) {
        //Arrange: Create transfer test data using data builder
        NewAccountsForTests testData = accountsDataFactory.createOneNewAccountForTest(
                originalCustomerId,
                originalAccountId,
                accountType
        );

        //Act: navigate to the Transfer Funds page, transfer to new account via UI, and verify correct UI message after transaction
        goTo.transferFunds();
        transferFundsPage.transferFunds(originalAccountId, testData.account1().id(), transferAmount);
        transferFundsPage.assertAmountResultIsTransferAmount(transferAmount);

        //Assert: Verify that new account's id and balance are visible in overview, and that balance is correct after transfer
        BigDecimal expectedBalanceAfterTransfer = testData.account1OriginalBalance().add(transferAmount);
        feHelper.goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(testData.account1().id(), expectedBalanceAfterTransfer);
    }

    @ParameterizedTest(name = "User can transfer {0} from {1} account and balance correctly decreases")
    @MethodSource("provideTransferAmountAndAccountType")
    void userShouldBeAbleToTransferFundsAndBalanceDecreases(BigDecimal transferAmount, AccountType accountType) {
        //Arrange: Create transfer test data using data builder
        NewAccountsForTests testData = accountsDataFactory.createOneNewAccountForTest(
                originalCustomerId,
                originalAccountId,
                accountType
        );

        //Act: navigate to the Transfer Funds page, transfer to new account via UI, and verify correct UI message after transaction
        goTo.transferFunds();
        transferFundsPage.transferFunds(testData.account1().id(), originalAccountId, transferAmount);
        transferFundsPage.assertAmountResultIsTransferAmount(transferAmount);

        //Assert: Verify that new account's id and balance are visible in overview, and that balance is correct after transfer
        BigDecimal expectedBalanceAfterTransfer = testData.account1OriginalBalance().subtract(transferAmount);
        feHelper.goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(testData.account1().id(), expectedBalanceAfterTransfer);
    }

    @ParameterizedTest(name = "Transaction of {0} to {1} account creates correct credit transaction record")
    @MethodSource("provideTransferAmountAndAccountType")
    void transactionCreatesCorrectCreditTransactionRecord(BigDecimal transferAmount, AccountType accountType) {
        //Arrange: Create transfer test data using data builder
        TransferTransactionData testData = transfersDataFactory.buildTestDataForTransferToNewAccount(
                transferAmount,
                accountType,
                originalCustomerId,
                originalAccountId
        );

        //Act: Navigate to the transaction details page and grab actual transaction from the UI
        goTo.transactionDetailsPage(testData.transaction().id());
        TransactionDto actualTransaction = transactionDetailsPage.toDto(testData.accountId());

        //Assert: Equality comparison between expected and actual transaction to verify transaction record's correctness
        Assertions.assertEquals(testData.transaction(), actualTransaction);
    }

    @ParameterizedTest(name = "Transaction of {0} from {1} account creates correct debit transaction record")
    @MethodSource("provideTransferAmountAndAccountType")
    void transactionCreatesCorrectDebitTransactionRecord(BigDecimal transferAmount, AccountType accountType) {
        //Arrange: Create transfer test data using data builder
        TransferTransactionData testData = transfersDataFactory.buildTestDataForTransferFromNewAccount(
                transferAmount,
                accountType,
                originalCustomerId,
                originalAccountId
        );

        //Act: Navigate to the transaction details page and grab actual transaction from the UI
        goTo.transactionDetailsPage(testData.transaction().id());
        TransactionDto actualTransaction = transactionDetailsPage.toDto(testData.accountId());

        //Assert: Equality comparison between expected and actual transaction to verify transaction record's correctness
        Assertions.assertEquals(testData.transaction(), actualTransaction);
    }

    //Method sources

    private static Stream<Arguments> provideTransferAmountAndAccountType() {
        return Stream.of(
                Arguments.of(new BigDecimal("5.00"), AccountType.CHECKING),
                Arguments.of(new BigDecimal("150.00"), AccountType.CHECKING),
                Arguments.of(new BigDecimal("10000.00"), AccountType.CHECKING),
                Arguments.of(new BigDecimal("5.00"), AccountType.SAVINGS),
                Arguments.of(new BigDecimal("150.00"), AccountType.SAVINGS),
                Arguments.of(new BigDecimal("10000.00"), AccountType.SAVINGS)
        );
    }

}
