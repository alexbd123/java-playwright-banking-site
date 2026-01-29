package com.example.qa.tests;

import com.example.qa.enums.AccountTypes;
import com.example.qa.models.User;
import com.example.qa.models.UserFactory;
import com.example.qa.pages.AccountsOverviewPage;
import com.example.qa.pages.NavigationPage;
import com.example.qa.pages.OpenNewAccountPage;
import com.example.qa.pages.RegistrationPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class OpenNewAccountTests extends BaseTest {

    private NavigationPage goTo;
    private OpenNewAccountPage openNewAccountPage;
    private AccountsOverviewPage accountsOverviewPage;

    @BeforeEach
    public void setUp() {
        RegistrationPage registrationPage = new RegistrationPage(page);
        User user = UserFactory.validRandomUser();
        registrationPage.registerValidUserAndLogInOrOut(user, true);
        goTo = new NavigationPage(page);
        goTo.openNewAccount();
        openNewAccountPage = new OpenNewAccountPage(page);
        accountsOverviewPage = new AccountsOverviewPage(page);
    }

    @ParameterizedTest(name = "User can open new {0} account and see it in overview")
    @EnumSource(AccountTypes.class)
    void userCanOpenNewAccountAndAccountAppearsInOverview(AccountTypes accountType) {
        openNewAccountPage.selectAccountTypeAndOpenNewAccount(accountType);
        assertThat(openNewAccountPage.successfullyOpenedAccountMessage()).isVisible();
        assertThat(openNewAccountPage.newAccountIdLink()).isEnabled();
        String newAccountNumber = openNewAccountPage.getAccountNumber();
        goTo.accountsOverview();
        assertThat(accountsOverviewPage.accountNumberLinkInAccountTable(newAccountNumber)).isVisible();
    }
}
