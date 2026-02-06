package com.example.qa.tests;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.example.qa.enums.AccountTypes;

import java.math.BigDecimal;

public class TransferFundsTests extends AuthenticatedBaseTest {

    protected static AccountActionsAPI accountActionsAPI;
    protected AccountDto originalCheckingAccount;


    @BeforeEach
    void initApiAndGetContext() {
        accountActionsAPI = new AccountActionsAPI(request);
        originalCheckingAccount = customerContext.getOriginalAccount();
    }

    @ParameterizedTest(name = "Funds transferred via API from original account to new {0} account appear in overview")
    @EnumSource(AccountTypes.class)
    void transferredFundsToNewAccountShouldAppearInOverview(AccountTypes accountType) {
        AccountDto newAccountToReceiveFunds = accountActionsAPI.createNewAccount(
                customerContext.getCustomerId(),
                accountType,
                originalCheckingAccount);
        BigDecimal transferAmount = BigDecimal.valueOf(100);
        BigDecimal expectedBalanceAfterTransfer = transferFundsAndReturnExpectedBalance(
                originalCheckingAccount,
                newAccountToReceiveFunds,
                transferAmount);
        goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(newAccountToReceiveFunds, expectedBalanceAfterTransfer);
    }  

    @ParameterizedTest(name = "Funds transferred via API from new {0} account to original account appear in overview")
    @EnumSource(AccountTypes.class)
    void transferredFundsToOriginalAccountShouldAppearInOverview(AccountTypes accountType) {
        AccountDto newAccountToSendFrom = accountActionsAPI.createNewAccount(
                customerContext.getCustomerId(),
                accountType,
                originalCheckingAccount);
        BigDecimal transferAmount = BigDecimal.valueOf(100);
        BigDecimal expectedBalanceAfterTransfer = transferFundsAndReturnExpectedBalance(
                newAccountToSendFrom,
                originalCheckingAccount,
                transferAmount);
        goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(originalCheckingAccount, expectedBalanceAfterTransfer);
    }

    //Test helpers

    public void goToOverviewAndWaitForTableVisibility() {
        goTo.accountsOverview();
        assertThat(accountsOverviewPage.accountTable()).isVisible();
    }

    public BigDecimal transferFundsAndReturnExpectedBalance(
            AccountDto fromAccount,
            AccountDto toAccount,
            BigDecimal transferAmount) {
        BigDecimal originalBalance = toAccount.getBalance();
        accountActionsAPI.sendPostRequestToTransferFunds(
                fromAccount,
                toAccount,
                transferAmount);
        return originalBalance.add(transferAmount);
    }
}
