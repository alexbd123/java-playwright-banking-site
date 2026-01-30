package com.example.qa.tests;

import com.example.qa.enums.AccountTypes;
import com.example.qa.tests.base_tests.AuthenticatedBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class OpenNewAccountTests extends AuthenticatedBaseTest {

    @BeforeEach
    public void navigateToOpenNewAccountsPage() {
        goTo.openNewAccount();
    }

    @ParameterizedTest(name = "User can open new {0} account and see it in overview")
    @EnumSource(AccountTypes.class)
    void userCanOpenNewAccountAndAccountAppearsInOverview(AccountTypes accountType) {
        String newAccountNumber = openNewAccountPage.openNewAccountAndReturnAccountNumber(accountType);
        assertThat(openNewAccountPage.successfullyOpenedAccountMessage()).isVisible();
        assertThat(openNewAccountPage.newAccountIdLink()).isEnabled();
        goTo.accountsOverview();
        assertThat(accountsOverviewPage.accountNumberLinkInAccountTable(newAccountNumber)).isVisible();
    }
}
