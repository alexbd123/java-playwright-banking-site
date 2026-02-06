package com.example.qa.tests;

import com.example.qa.api.clients.AccountActionsAPI;
import com.example.qa.api.dtos.AccountDto;
import com.example.qa.enums.AccountTypes;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OpenNewAccountTests extends AuthenticatedBaseTest {

    protected static AccountActionsAPI accountActionsAPI;
    private static Integer newCheckingAccountId;
    private static Integer newSavingsAccountId;

    @BeforeAll
    public static void setUpAccountTestData() {
        accountActionsAPI = new AccountActionsAPI(request);

        AccountDto newCheckingAccount = accountActionsAPI.createNewAccount(customerContext.getCustomerId(),
                AccountTypes.CHECKING,
                customerContext.getOriginalAccountId());
        newCheckingAccountId = newCheckingAccount.getId();

        AccountDto newSavingsAccount = accountActionsAPI.createNewAccount(customerContext.getCustomerId(),
                AccountTypes.SAVINGS,
                customerContext.getOriginalAccountId());
        newSavingsAccountId = newSavingsAccount.getId();
    }

    @BeforeEach
    public void navigateToOpenNewAccountsPage() {
        goTo.accountsOverview();
        assertThat(accountsOverviewPage.accountTable()).isVisible();
    }

    @ParameterizedTest(name = "{0} account created via API is visible in Account Overview")
    @EnumSource(AccountTypes.class)
    void newAccountShouldAppearInOverview(AccountTypes accountType) {
        Integer newAccountNumber = accountType == AccountTypes.CHECKING ? newCheckingAccountId : newSavingsAccountId;
        assertThat(accountsOverviewPage.accountNumberLinkInAccountTable(String.valueOf(newAccountNumber))).isVisible();
    }
}
