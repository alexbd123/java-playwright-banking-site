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
import java.math.RoundingMode;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OpenNewAccountTests extends AuthenticatedBaseTest {

    protected static AccountActionsAPI accountActionsAPI;
    protected AccountDto originalCheckingAccount;
    private final BigDecimal DEFAULT_BALANCE = BigDecimal.valueOf(100);

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
                originalCheckingAccount);
        goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(newAccount, DEFAULT_BALANCE);
    }

    @ParameterizedTest(name="Funds deposited via API into new {0} account are visible in Account Overview")
    @EnumSource(AccountTypes.class)
    void depositedFundsShouldAppearInOverview(AccountTypes accountType) {
        AccountDto newAccount = accountActionsAPI.createNewAccount(
                customerContext.getCustomerId(),
                accountType,
                originalCheckingAccount);
        BigDecimal fundsToDeposit = BigDecimal.valueOf(100);
        accountActionsAPI.sendPostRequestToDepositFunds(newAccount, fundsToDeposit);
        BigDecimal expectedBalance = newAccount.getBalance()
                .add(fundsToDeposit);
        goToOverviewAndWaitForTableVisibility();
        accountsOverviewPage.assertThatBalanceIsVisibleAndAmountIsCorrect(newAccount, expectedBalance);
    }

    public void goToOverviewAndWaitForTableVisibility() {
        goTo.accountsOverview();
        assertThat(accountsOverviewPage.accountTable()).isVisible();
    }

}
