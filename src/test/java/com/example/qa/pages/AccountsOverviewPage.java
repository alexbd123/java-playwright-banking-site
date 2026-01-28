package com.example.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class AccountsOverviewPage {
    private final Page page;
    private final Locator accountTable;

    public AccountsOverviewPage(Page page) {
        this.page = page;
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
}