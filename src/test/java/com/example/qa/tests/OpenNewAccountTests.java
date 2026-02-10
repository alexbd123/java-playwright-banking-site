package com.example.qa.tests;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.enums.AccountTypes;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OpenNewAccountTests extends AuthenticatedBaseTest {

    private final BigDecimal DEFAULT_BALANCE = BigDecimal.valueOf(100);

    protected static AccountActionsAPI accountActionsAPI;
    protected AccountDto originalCheckingAccount;

    @BeforeEach
    void initApiClients() {
        accountActionsAPI = new AccountActionsAPI(request);
        originalCheckingAccount = customerContext.getOriginalAccount();
    }

    @ParameterizedTest(name="{0} account created via API is visible in Account Overview with correct default balance")
    @EnumSource(AccountTypes.class)
    void newAccountShouldAppearInOverviewWithCorrectBalance(AccountTypes accountType) {
        AccountDto newAccount = accountActionsAPI.createNewAccount(
                customerContext.getCustomerId(),
                accountType,
                originalCheckingAccount.id());
        goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(newAccount.id(), DEFAULT_BALANCE);
    }

    @ParameterizedTest(name="Funds deposited via API into new {0} account are visible in Account Overview")
    @EnumSource(AccountTypes.class)
    void depositedFundsShouldAppearInOverview(AccountTypes accountType) {
        AccountDto newAccount = accountActionsAPI.createNewAccount(
                customerContext.getCustomerId(),
                accountType,
                originalCheckingAccount.id());
        BigDecimal fundsToDeposit = new BigDecimal("100.00");

        accountActionsAPI.sendPostRequestToDepositFunds(newAccount.id(), fundsToDeposit);

        BigDecimal expectedBalance = accountActionsAPI.getAccountById(newAccount.id()).balance();
        goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(newAccount.id(), expectedBalance);
    }

    public void goToOverviewAndWaitForTableVisibility() {
        goTo.accountsOverview();
        assertThat(accountsOverviewPage.accountTable()).isVisible();
    }

}
