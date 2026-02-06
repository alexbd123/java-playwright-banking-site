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
    void initApiClients() {
        accountActionsAPI = new AccountActionsAPI(request);
        originalCheckingAccount = customerContext.getOriginalAccount();
    }

    @ParameterizedTest(name = "Funds transferred via API from original account to new {0} account appear in overview")
    @EnumSource(AccountTypes.class)
    void transferredFundsToNewAccountShouldAppearInOverview(AccountTypes accountType) {
        AccountDto toNewAccount = accountActionsAPI.createNewAccount(
                customerContext.getCustomerId(),
                accountType,
                originalCheckingAccount);
        toNewAccount.setBalance(BigDecimal.valueOf(100));
        BigDecimal originalBalance = toNewAccount.getBalance();
        BigDecimal transferAmount = BigDecimal.valueOf(100);
        accountActionsAPI.sendPostRequestToTransferFunds(
                originalCheckingAccount,
                toNewAccount,
                transferAmount);
        BigDecimal expectedBalance = originalBalance.add(BigDecimal.valueOf(100));
        goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(toNewAccount, expectedBalance);
    }  

    @ParameterizedTest(name = "Funds transferred via API from new {0} account to original account appear in overview")
    @EnumSource(AccountTypes.class)
    void transferredFundsToOriginalAccountShouldAppearInOverview(AccountTypes accountType) {
        AccountDto fromNewAccount = accountActionsAPI.createNewAccount(
                customerContext.getCustomerId(),
                accountType,
                originalCheckingAccount);
        BigDecimal originalBalance = originalCheckingAccount.getBalance();
        BigDecimal transferAmount = BigDecimal.valueOf(100);
        accountActionsAPI.sendPostRequestToTransferFunds(
                fromNewAccount,
                originalCheckingAccount,
                transferAmount);
        BigDecimal expectedBalance = originalBalance.add(transferAmount);
        goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(originalCheckingAccount, expectedBalance);
    }

    //Small helpers

    public void goToOverviewAndWaitForTableVisibility() {
        goTo.accountsOverview();
        assertThat(accountsOverviewPage.accountTable()).isVisible();
    }
}
