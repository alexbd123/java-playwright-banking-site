package com.example.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AccountsOverviewPage {
    private final Locator accountTable;

    public AccountsOverviewPage(Page page) {
        this.accountTable = page.locator("#accountTable");
    }

    //Expose Locators

    public Locator accountTable() {
        return accountTable;
    }

    public Locator accountNumberLinkInAccountTable(String accountNumber) {
        return accountTable.getByRole(AriaRole.LINK,
                new Locator.GetByRoleOptions().setName(accountNumber.trim()).setExact(true));
    }

    public Locator accountBalanceInTable(String accountNumber) {
        return accountNumberLinkInAccountTable(accountNumber).locator("xpath=ancestor::td/following-sibling::td[1]");
    }

    //Actions

    public void goToAccountActivity(int accountNumber) {
        accountNumberLinkInAccountTable(String.valueOf(accountNumber)).click();
    }

    //Page helpers

    public String convertBigDecimalToExpectedBalanceString(BigDecimal value) {
        boolean negative = value.signum() < 0;
        BigDecimal absValue = value.abs().setScale(2, RoundingMode.HALF_UP);
        return (negative ? "-$" : "$") + absValue;
    }

    public void assertBalanceVisibility(int accountId, BigDecimal expectedBalance) {
        assertThat(accountBalanceInTable(String.valueOf(accountId)))
                .hasText(convertBigDecimalToExpectedBalanceString(expectedBalance));
    }

}