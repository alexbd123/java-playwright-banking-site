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
    private static AccountDto newCheckingAccount;
    private static AccountDto newSavingsAccount;

    @BeforeEach
    void setUpAccountTestDataPerTest() {
        accountActionsAPI = new AccountActionsAPI(request);

        newCheckingAccount = accountActionsAPI.createNewAccount(customerContext.getCustomerId(),
                AccountTypes.CHECKING,
                customerContext.getOriginalAccountId());

        newSavingsAccount = accountActionsAPI.createNewAccount(customerContext.getCustomerId(),
                AccountTypes.SAVINGS,
                customerContext.getOriginalAccountId());

        //Quirk of Parabank is accounts created via API have balance of 0 in backend, but 100 in UI
        newCheckingAccount.setBalance(BigDecimal.valueOf(100));
        newSavingsAccount.setBalance(BigDecimal.valueOf(100));
    }

    @ParameterizedTest(name="{0} account created via API is visible in Account Overview with correct default balance")
    @EnumSource(AccountTypes.class)
    void newAccountShouldAppearInOverviewWithCorrectBalance(AccountTypes accountType) {
        AccountDto accountBeingTested = accountType == AccountTypes.CHECKING ? newCheckingAccount : newSavingsAccount;
        goToOverviewAndWaitForTableVisibility();
        assertThat(accountsOverviewPage.accountNumberLinkInAccountTable(String.valueOf(accountBeingTested.getId()))).isVisible();
        assertThatBalanceIsVisibleAndAmountIsCorrect(accountBeingTested.getId(), accountBeingTested.getBalance());
    }

    @ParameterizedTest(name="Funds deposited via API into new {0} account are visible in Account Overview")
    @EnumSource(AccountTypes.class)
    void depositedFundsShouldAppearInOverview(AccountTypes accountType) {
        AccountDto accountBeingTested = accountType == AccountTypes.CHECKING ? newCheckingAccount : newSavingsAccount;
        BigDecimal fundsToDeposit = BigDecimal.valueOf(100);
        accountActionsAPI.sendPostRequestToDepositFunds(accountBeingTested.getId(), fundsToDeposit);
        BigDecimal expectedBalance = accountBeingTested.getBalance()
                .add(fundsToDeposit);
        goToOverviewAndWaitForTableVisibility();
        assertThatBalanceIsVisibleAndAmountIsCorrect(accountBeingTested.getId(), expectedBalance);
    }

    //Small helpers

    public String convertBigDecimalToExpectedBalanceString(BigDecimal bigDecimal) {
        return "$" + bigDecimal.setScale(2, RoundingMode.HALF_UP);
    }

    public void goToOverviewAndWaitForTableVisibility() {
        goTo.accountsOverview();
        assertThat(accountsOverviewPage.accountTable()).isVisible();
    }

    public void assertThatBalanceIsVisibleAndAmountIsCorrect(int accountId, BigDecimal expectedBalance) {
        assertThat(accountsOverviewPage.accountBalanceInTable(String.valueOf(accountId)))
                .hasText(convertBigDecimalToExpectedBalanceString(expectedBalance));
    }
}
