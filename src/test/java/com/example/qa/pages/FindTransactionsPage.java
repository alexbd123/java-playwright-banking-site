package com.example.qa.pages;

import com.example.qa.enums.TransactionType;
import com.example.qa.tests.ui.TestHelper;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.math.BigDecimal;
import java.util.Objects;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FindTransactionsPage {

    private final Page page;
    private final TestHelper feHelper;
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
        this.feHelper = new TestHelper(page);
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

    //Expose Locators


    private Locator getTransactionRow(int transactionId) {
        return page.locator(String.format("tr:has(a[href*='id=%d'])", transactionId));
    }

    private Locator getTransactionLink(Locator row) {
        return row.locator("td:nth-of-type(2) a");
    }

    public Locator getDebitCell(int transactionId) {
        return getTransactionRow(transactionId).locator("td:nth-of-type(3)");
    }

    public Locator getCreditCell(int transactionId) {
        return getTransactionRow(transactionId).locator("td:nth-of-type(4)");
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

    public void clickFindByAmountButton() {
        findByAmountButton.click();
    }

    public void enterTransactionAmount(BigDecimal amount) {
        findByAmountInput.fill(String.valueOf(amount));
    }

    public void clickTransactionLink(int transactionId) {
        getTransactionLink(getTransactionRow(transactionId)).click();
    }

    public void findTransactionByDate(int accountId, String transactionDate) {
        selectAccountId(accountId);
        enterTransactionDate(transactionDate);
        clickFindByDateButton();
    }

    public void findTransactionByAmount(int accountId, BigDecimal amount) {
        selectAccountId(accountId);
        enterTransactionAmount(amount);
        clickFindByAmountButton();
    }

    public void goToTransactionDetails(int transactionId) {
        clickTransactionLink(transactionId);
    }

    public void verifyTransactionTypeAndAmountInTable(TransactionType transactionType, int transactionId, BigDecimal amount) {
        Locator transactionTypeCell = Objects.equals(transactionType, TransactionType.DEBIT)
                ? getDebitCell(transactionId)
                : getCreditCell(transactionId);
        assertThat(transactionTypeCell).hasText(feHelper.formatBigDecimalToString(amount));
    }

}
