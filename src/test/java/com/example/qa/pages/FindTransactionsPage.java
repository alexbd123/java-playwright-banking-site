package com.example.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class FindTransactionsPage {

    private final Page page;
    private final Locator accountIdSelect;
    private final Locator transactionIdInput;
    private final Locator findTransactionByIdButton;
    private final Locator findByDateInput;
    private final Locator findByDateButton;
    private final Locator findByDateFromDateInput;
    private final Locator findByDateToDateInput;
    private final Locator findByDateRangeButton;
    private final Locator findByAmountInput;
    private final Locator findByAmountButton;

    public FindTransactionsPage(Page page) {
        this.page = page;
        this.accountIdSelect = page.locator("#accountId");
        this.transactionIdInput = page.locator("#transactionId");
        this.findTransactionByIdButton = page.locator("#findById");
        this.findByDateInput = page.locator("#transactionDate");
        this.findByDateButton = page.locator("#findByDate");
        this.findByDateFromDateInput = page.locator("#fromDate");
        this.findByDateToDateInput = page.locator("#toDate");
        this.findByDateRangeButton = page.locator("#findDateByRange");
        this.findByAmountInput = page.locator("#amount");
        this.findByAmountButton = page.locator("#findByAmount");
    }

    public void selectAccountId(int accountId) {
        accountIdSelect.selectOption(String.valueOf(accountId));
    }

    public void enterTransactionId(int transactionId) {
        transactionIdInput.fill(String.valueOf(transactionId));
    }

    public void clickGetTransactionByIdButton() {
        findTransactionByIdButton.click();
    }

    public void enterTransactionDate(String transactionDate) {
        findByDateInput.fill(transactionDate);
    }

    public void clickFindByDateButton() {
        findByDateButton.click();
    }

    public void clickTransactionLink(int transactionId) {
        String selector = String.format("a[href*='transaction.htm?id=%d']", transactionId);
        page.locator(selector).click();
    }

    public void findTransactionByDate(int accountId, int transactionId, String transactionDate) {
        selectAccountId(accountId);
        enterTransactionDate(transactionDate);
        clickFindByDateButton();
        clickTransactionLink(transactionId);
    }

}
