package com.example.qa.tests.ui;

import com.example.qa.pages.AccountsOverviewPage;
import com.example.qa.pages.NavigationPage;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TestHelper {

    private final NavigationPage goTo;
    private final AccountsOverviewPage accountsOverviewPage;

    public TestHelper(Page page) {
        this.goTo = new NavigationPage(page);
        this.accountsOverviewPage = new AccountsOverviewPage(page);
    }

    public void goToOverviewAndWaitForTableVisibility() {
        goTo.accountsOverview();
        assertThat(accountsOverviewPage.accountTable()).isVisible();
    }
}
