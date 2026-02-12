package com.example.qa.tests.ui.accounts;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
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


    @BeforeAll
    void initApiAndGetContext() {
        accountActionsAPI = new AccountActionsAPI(request);
        originalCheckingAccount = customerContext.getOriginalAccount();
        originalAccountId = originalCheckingAccount.id();
        originalCustomerId = customerContext.getCustomerId();
    }

    @ParameterizedTest(name = "User can transfer {0} to {1} account and balance correctly increases")
    @MethodSource("provideTransferAmountAndAccountType")
    void userShouldBeAbleToTransferFundsAndBalanceIncreases(BigDecimal transferAmount, AccountTypes accountType) {
        AccountDto newAccountToReceiveFunds = accountActionsAPI.createNewAccount(
                originalCustomerId,
                accountType,
                originalCheckingAccount.id());
        int newAccountId = newAccountToReceiveFunds.id();
        BigDecimal accountBalance = retrieveAccountBalance(originalCustomerId, newAccountId);

        goTo.transferFunds();
        transferFundsPage.transferFunds(originalAccountId, newAccountId, transferAmount);
        transferFundsPage.assertAmountResultIsTransferAmount(transferAmount);

        BigDecimal expectedBalanceAfterTransfer = accountBalance.add(transferAmount);
        goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(newAccountId, expectedBalanceAfterTransfer);
    }

    @ParameterizedTest(name = "User can transfer {0} from {1} account and balance correctly decreases")
    @MethodSource("provideTransferAmountAndAccountType")
    void userShouldBeAbleToTransferFundsAndBalanceDecreases(BigDecimal transferAmount, AccountTypes accountType) {
        AccountDto newAccountToSendFundsFrom = accountActionsAPI.createNewAccount(
                originalCustomerId,
                accountType,
                originalCheckingAccount.id());
        int newAccountId = newAccountToSendFundsFrom.id();
        BigDecimal accountBalance = retrieveAccountBalance(originalCustomerId, newAccountId);

        goTo.transferFunds();
        transferFundsPage.transferFunds(newAccountId, originalAccountId, transferAmount);
        transferFundsPage.assertAmountResultIsTransferAmount(transferAmount);

        BigDecimal expectedBalanceAfterTransfer = accountBalance.subtract(transferAmount);
        goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(newAccountId, expectedBalanceAfterTransfer);
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

    public BigDecimal retrieveAccountBalance(int customerId, int accountId) {
        return accountActionsAPI.sendGetRequestForCustomerAccountsInfo(
                        customerId).stream()
                .filter(a -> a.id() == accountId)
                .map(AccountDto::balance)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

}
