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
                originalCheckingAccount.id());
        BigDecimal transferAmount = new BigDecimal("100.00");

        accountActionsAPI.sendPostRequestToTransferFunds(
                originalCheckingAccount.id(),
                newAccountToReceiveFunds.id(),
                transferAmount);

        BigDecimal expectedBalanceAfterTransfer = accountActionsAPI.getAccountById(newAccountToReceiveFunds.id()).balance();
        goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(newAccountToReceiveFunds.id(), expectedBalanceAfterTransfer);
    }

    @ParameterizedTest(name = "Funds transferred via API from new {0} account to original account appear in overview")
    @EnumSource(AccountTypes.class)
    void transferredFundsToOriginalAccountShouldAppearInOverview(AccountTypes accountType) {
        AccountDto newAccountToSendFrom = accountActionsAPI.createNewAccount(
                customerContext.getCustomerId(),
                accountType,
                originalCheckingAccount.id());
        BigDecimal transferAmount = new BigDecimal("100.00");

        accountActionsAPI.sendPostRequestToTransferFunds(
                newAccountToSendFrom.id(),
                originalCheckingAccount.id(),
                transferAmount);

        BigDecimal expectedBalanceAfterTransfer = accountActionsAPI.getAccountById(originalCheckingAccount.id()).balance();
        goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(originalCheckingAccount.id(), expectedBalanceAfterTransfer);
    }

    //Test helpers

    public void goToOverviewAndWaitForTableVisibility() {
        goTo.accountsOverview();
        assertThat(accountsOverviewPage.accountTable()).isVisible();
    }

}
