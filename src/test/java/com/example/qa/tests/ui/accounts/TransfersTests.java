package com.example.qa.tests.ui.accounts;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.api.dtos.TransactionDto;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.example.qa.enums.AccountTypes;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransfersTests extends AuthenticatedBaseTest {

    protected static AccountActionsAPI accountActionsAPI;
    protected AccountDto originalCheckingAccount;
    protected int originalAccountId;
    protected int originalCustomerId;


    @BeforeAll
    void initApiAndGetContext() {
        accountActionsAPI = new AccountActionsAPI(request);
        originalCheckingAccount = customerContext.originalAccount();
        originalAccountId = originalCheckingAccount.id();
        originalCustomerId = customerContext.customerId();
    }

    @ParameterizedTest(name = "User can transfer {0} to {1} account")
    @MethodSource("provideTransferAmountAndAccountType")
    void userShouldBeAbleToTransferFundsAndBalanceIncreases(BigDecimal transferAmount, AccountTypes accountType) {
        //Arrange: create new account to receive funds via API and grab ID and account balance
        AccountDto newAccountToReceiveFunds = accountActionsAPI.createNewAccount(
                originalCustomerId,
                accountType,
                originalCheckingAccount.id());
        int newAccountId = newAccountToReceiveFunds.id();
        BigDecimal accountBalance = beHelper.retrieveAccountBalance(originalCustomerId, newAccountId);

        //Act: navigate to the Transfer Funds page, transfer to new account via UI, and verify correct UI message after transaction
        goTo.transferFunds();
        transferFundsPage.transferFunds(originalAccountId, newAccountId, transferAmount);
        transferFundsPage.assertAmountResultIsTransferAmount(transferAmount);

        //Assert: Verify that new account's id and balance are visible in overview, and that balance is correct after transfer
        BigDecimal expectedBalanceAfterTransfer = accountBalance.add(transferAmount);
        goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(newAccountId, expectedBalanceAfterTransfer);
    }

    @ParameterizedTest(name = "User can transfer {0} from {1} account and balance correctly decreases")
    @MethodSource("provideTransferAmountAndAccountType")
    void userShouldBeAbleToTransferFundsAndBalanceDecreases(BigDecimal transferAmount, AccountTypes accountType) {
        //Arrange: create new account to transfer funds from via API and grab ID and account balance
        AccountDto newAccountToSendFundsFrom = accountActionsAPI.createNewAccount(
                originalCustomerId,
                accountType,
                originalCheckingAccount.id());
        int newAccountId = newAccountToSendFundsFrom.id();
        BigDecimal accountBalance = beHelper.retrieveAccountBalance(originalCustomerId, newAccountId);

        //Act: navigate to the Transfer Funds page, transfer to new account via UI, and verify correct UI message after transaction
        goTo.transferFunds();
        transferFundsPage.transferFunds(newAccountId, originalAccountId, transferAmount);
        transferFundsPage.assertAmountResultIsTransferAmount(transferAmount);

        //Assert: Verify that new account's id and balance are visible in overview, and that balance is correct after transfer
        BigDecimal expectedBalanceAfterTransfer = accountBalance.subtract(transferAmount);
        goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(newAccountId, expectedBalanceAfterTransfer);
    }

    @ParameterizedTest(name = "Transaction of {0} to {1} account creates correct credit transaction record")
    @MethodSource("provideTransferAmountAndAccountType")
    void transactionCreatesCorrectCreditTransactionRecord(BigDecimal transferAmount, AccountTypes accountType) {
        //Arrange: Create new account to receive transfer funds via API and grab id, transfer funds to this account via API and determine the expected transaction
        AccountDto newAccountToReceiveFunds = accountActionsAPI.createNewAccount(
                originalCustomerId,
                accountType,
                originalCheckingAccount.id());
        int newAccountId = newAccountToReceiveFunds.id();
        List<TransactionDto> transactionsBeforeTransfer = accountActionsAPI.sendGetRequestForAllTransactionsForAccount(newAccountId);
        accountActionsAPI.sendPostRequestToTransferFunds(originalAccountId, newAccountId, transferAmount);
        TransactionDto expectedTransaction = beHelper.determineNewTransactionFromList(transactionsBeforeTransfer, newAccountId);

        //Act: Navigate to the transaction details page and grab actual transaction from the UI
        goTo.transactionDetailsPage(expectedTransaction.id());
        TransactionDto actualTransaction = transactionDetailsPage.toDto(newAccountId);

        //Assert: Equality comparison between expected and actual transaction to verify transaction record's correctness
        Assertions.assertEquals(expectedTransaction, actualTransaction);
    }

    @ParameterizedTest(name = "Transaction of {0} from {1} account creates correct debit transaction record")
    @MethodSource("provideTransferAmountAndAccountType")
    void transactionCreatesCorrectDebitTransactionRecord(BigDecimal transferAmount, AccountTypes accountType) {
        //Arrange: Create new account to transfer funds from via API and grab id, transfer funds from this account via API and determine the expected transaction
        AccountDto newAccountToTransferFrom = accountActionsAPI.createNewAccount(
                originalCustomerId,
                accountType,
                originalCheckingAccount.id());
        int newAccountId = newAccountToTransferFrom.id();
        List<TransactionDto> transactionsBeforeTransfer = accountActionsAPI.sendGetRequestForAllTransactionsForAccount(newAccountId);
        accountActionsAPI.sendPostRequestToTransferFunds(newAccountId, originalAccountId , transferAmount);
        TransactionDto expectedTransaction = beHelper.determineNewTransactionFromList(transactionsBeforeTransfer, newAccountId);

        //Act: Navigate to the transaction details page and grab actual transaction from the UI
        goTo.transactionDetailsPage(expectedTransaction.id());
        TransactionDto actualTransaction = transactionDetailsPage.toDto(newAccountId);

        //Assert: Equality comparison between expected and actual transaction to verify transaction record's correctness
        Assertions.assertEquals(expectedTransaction, actualTransaction);
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

    //Test helpers

    public void goToOverviewAndWaitForTableVisibility() {
        goTo.accountsOverview();
        assertThat(accountsOverviewPage.accountTable()).isVisible();
    }

}
