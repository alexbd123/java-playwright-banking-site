package com.example.qa.pages;

import com.example.qa.api.dtos.AccountDto;
import com.example.qa.enums.AccountTypes;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.math.BigDecimal;

public class AccountActivityPage {

    private final Page page;
    private final Locator activityPeriodSelect;
    private final Locator activityTypeSelect;
    private final Locator transactionTable;
    private final Locator accountIdCell;
    private final Locator accountTypeCell;
    private final Locator accountBalanceCell;
    private final Locator availableBalanceCell;
    private final Locator transactionLinks;

    public AccountActivityPage(Page page) {
        this.page = page;
        this.activityPeriodSelect = page.locator("#month");
        this.activityTypeSelect = page.locator("#transactionType");
        this.transactionTable = page.locator("#transactionTable");
        this.accountIdCell = page.locator("#accountId");
        this.accountTypeCell = page.locator("#accountType");
        this.accountBalanceCell = page.locator("#balance");
        this.availableBalanceCell = page.locator("#availableBalance");
        this.transactionLinks = page.locator("a[href*='transaction.htm?id']");
    }

    //Expose Locators

    public int getAccountId() {
        return Integer.parseInt(accountIdCell.innerText());
    }

    public AccountTypes getAccountType() {
        return AccountTypes.valueOf(accountTypeCell.innerText());
    }

    public BigDecimal getBalance() {
        String raw = accountBalanceCell.innerText().trim();
        String cleaned = raw.replaceAll("[^0-9.\\-]", "");
        return new BigDecimal(cleaned);
    }

    public Locator getTransactionLink(int transactionId) {
        String selector = String.format("a[href*='transaction.htm?id=%d']", transactionId);
        return transactionLinks.filter(new Locator.FilterOptions().setHas(page.locator(selector)));
    }

    public String getTransactionDate(int transactionId) {
        return getTransactionLink(transactionId).locator("xpath=preceding-sibling::td[1]").innerText();
    }

    public BigDecimal getCreditAmount(int transactionId) {
        String raw = getTransactionLink(transactionId).locator("xpath=following-sibling::td[1]").innerText();
        String cleaned = raw.replaceAll("[^0-9.\\-]", "");
        return new BigDecimal(cleaned);
    }

    public BigDecimal getDebitAmount(int transactionId) {
        String raw = getTransactionLink(transactionId).locator("xpath=following-sibling::td[2]").innerText();
        String cleaned = raw.replaceAll("[^0-9.\\-]", "");
        return new BigDecimal(cleaned);
    }

    // Actions

    public AccountDto toDto (int customerId) {
        return new AccountDto(
                getAccountId(),
                customerId,
                getAccountType(),
                getBalance()
        );
    }

    public void clickTransactionLink(int transactionId) {
        String selector = String.format("a[href*='transaction.htm?id=%d']", transactionId);
        page.locator(selector).click();
    }

}
